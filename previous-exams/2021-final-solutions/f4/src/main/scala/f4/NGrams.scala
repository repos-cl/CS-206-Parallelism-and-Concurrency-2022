package f4

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.log4j.{Logger, Level}

import org.apache.spark.rdd.RDD

import scala.util.Properties.isWin

/** This class calculates word n-gram frequencies in the text it receives, but
  * the implementation is faulty and the result is calculated locally.
  *
  * Your tasks will revolve around fixing the implementation, making the
  * calculation distributed and finally, calculating per-speaker word n-gram
  * frequencies.
  */
abstract class NGrams {
  type Triple[T] = (T, T, T)
  type NGramMap = Map[(String, String, String), Int]

  val sc: SparkContext
  val lines: Seq[String]

  val punctuationRx = "[-,:;?!.'\"]+".r
  def removePunctuation(str: String) = punctuationRx.replaceAllIn(str, "")
  def splitIntoWords(str: String) = str.trim.split("\\s+")

  /** Splits a string into a sequence of n-grams.
    *
    * There are two issues with the implementation:
    * [TASK 1]
    * Capitalization of words should not matter.
    * Convert all words in n-grams to lower case.
    *
    * [TASK 2]
    * If a sentence has less than 3 words, "fake" words should be added to its end.
    * For such sentences, add empty strings at the end.
    */
  def splitIntoNGrams(line: String): List[Triple[String]] = {
    
    val words = splitIntoWords(removePunctuation(line).toLowerCase)

    words.sliding(3).map { seq =>
      def at(i: Int) = if i < seq.length then seq(i) else ""
      (at(0), at(1), at(2))
    }.toList
      }

  def createNGramsRDD(lines: Seq[String]): RDD[List[Triple[String]]] =
    sc.parallelize(lines).map(splitIntoNGrams)

  /** This function _locally_ calculates the frequency of n-grams it receives. */
  def localNGrams(ngrams: RDD[List[Triple[String]]]): NGramMap = {
    ngrams.toLocalIterator
      .flatMap(ngramSeq => ngramSeq.map { ng =>
        Map(ng -> 1)
      })
      .reduce { (left, right) =>
        right.foldLeft(left) {
          case (left, (ng, weightA)) =>
            left.updatedWith(ng)(weightBOpt => Some(weightA + weightBOpt.getOrElse(0)))
        }
      }
  }

  /** [TASK 3] Based on the above code, calculate n-gram frequency in a
    * distributed manner. Define [[aggregateNGrams_zero]],
    * [[aggregateNGrams_seqOp]], [[aggregateNGrams_combOp]] in such a way that
    * this function will return the same result as the one above.
    */
  final def aggregateNGrams(ngrams: RDD[List[Triple[String]]]): NGramMap =
    ngrams.aggregate(aggregateNGrams_zero)(aggregateNGrams_seqOp, aggregateNGrams_combOp)

  def aggregateNGrams_zero: NGramMap =
    
    Map.empty[Triple[String], Int]
    
  def aggregateNGrams_seqOp(acc: NGramMap, ngramSeq: List[Triple[String]]) =
    
    ngramSeq.foldLeft(acc) {
      case (acc, ng) =>
        acc.updatedWith(ng)(w => Some(w.getOrElse(0) + 1))
    }
    
  def aggregateNGrams_combOp(acc: NGramMap, ngramMap: NGramMap) =
    
    ngramMap.foldLeft(acc) {
      case (acc, (ng, w)) =>
        acc.updatedWith(ng)(ww => Some(w + ww.getOrElse(0)))
    }
    
  /** Your two final tasks are about using Spark to calculate the n-gram frequency
    * per each speaker in the play.
    *
    * [TASK 4]
    * To extract each line's speaker from the text, implement [[createSpeakerLinesRDD]].
    *
    * [TASK 5]
    * To calculate the n-gram frequency, define [[createSpeakerLineNGramsRDD]]
    * and [[createSpeakerNGramsRDD]].
    */
  final def calculateSpeakerLineNGrams(lines: Seq[String]): Map[String, NGramMap] =
    createSpeakerNGramsRDD(createSpeakerLineNGramsRDD(createSpeakerLinesRDD(lines)))
      .toLocalIterator.toMap

  /** Use [[lines]] to create an RDD of (Speaker, Line) pairs.
    *
    * Each line in [[lines]] looks like "SPEAKER: LINE". To separate the two,
    * consider using the `.split` method on [[String]]. You can assume that the
    * speaker's name doesn't contain a colon.
    */
  def createSpeakerLinesRDD(lines: Seq[String]): RDD[(String, String)] =
    
    sc.parallelize(lines)
      .map { s =>
        val Array(speaker, line) = s.split(":", 2)
        assert(line != "", speaker)
        speaker -> line
      }
    
  /** Takes the result of [[createSpeakerLinesRDD]] and calculates n-grams for each
    * sentence, WITHOUT aggregating by speaker. Example:
    * ```
    *   val input = Seq(
    *     "First Citizen" -> "Before we proceed! Before we proceed, hear me speak."
    *     "First Citizen" -> "Hear me speak."
    *     "SEBASTIAN" -> "Art thou waking?"
    *   )
    *
    *   val output = Seq(
    *     "First Citizen" -> Map(
    *       ("before", "we", "proceed") -> 2,
    *       ("hear", "me", "speak") -> 1,
    *       // other n-grams following from the first sentence...
    *     ),
    *     "First Citizen" -> Map(
    *       ("hear", "me", "speak") -> 1,
    *     ),
    *     "SEBASTIAN" -> Map(
    *       ("art", "thou", "waking") -> 1,
    *     ),
    *   )
    * ```
    */
  def createSpeakerLineNGramsRDD(
    speakerLines: RDD[(String, String)]
  ): RDD[(String, NGramMap)] =
    
    speakerLines
      .mapValues { s =>
        splitIntoNGrams(s)
          .map(List(_))
          .foldLeft(aggregateNGrams_zero)(aggregateNGrams_seqOp)
      }
    
  /** Takes the result of [[createSpeakerLineNGramsRDD]] and aggregates the n-grams
    * per each speaker. Example:
    * ```
    *   val input = Seq(
    *     "First Citizen" -> Map(
    *       ("before", "we", "proceed") -> 1,
    *       ("hear", "me", "speak") -> 1,
    *     ),
    *     "First Citizen" -> Map(("hear", "me", "speak") -> 1),
    *     "SEBASTIAN" -> Map(("art", "thou", "waking") -> 1),
    *     "SEBASTIAN" -> Map(("art", "thou", "waking") -> 1),
    *   )
    *
    *   val output = Seq(
    *     "First Citizen" -> Map(
    *       ("before", "we", "proceed") -> 1,
    *       ("hear", "me", "speak") -> 2,
    *     ),
    *     "SEBASTIAN" -> Map(("art", "thou", "waking") -> 2),
    *   )
    * ```
    */
  def createSpeakerNGramsRDD(
    speakerLineNGrams: RDD[(String, NGramMap)]
  ): RDD[(String, NGramMap)] =
    
    speakerLineNGrams
      .groupByKey
      .mapValues(_.reduce(aggregateNGrams_combOp))
    }

object NGrams extends NGrams {
  // Reduce Spark logging verbosity
  Logger.getLogger("org").setLevel(Level.ERROR)

  val conf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("NGram")

  val sc: SparkContext = new SparkContext(conf)
  val lines = F4Data.lines

  def main(args: Array[String]): Unit = {
    timed("main", {
      val rdd =
        createSpeakerNGramsRDD(createSpeakerLineNGramsRDD(createSpeakerLinesRDD(lines)))
      rdd.toLocalIterator.foreach { p =>
        println(p)
      }
    })
  }

  val timing = new StringBuilder
  def timed[T](label: String, code: => T): T = {
    val start = System.currentTimeMillis()
    val result = code
    val stop = System.currentTimeMillis()
    timing.append(s"Processing $label took ${stop - start} ms.\n")
    result
  }
}
