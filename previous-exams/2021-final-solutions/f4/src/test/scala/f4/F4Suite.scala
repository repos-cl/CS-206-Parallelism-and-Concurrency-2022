package f4

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

class F4Suite extends munit.FunSuite, HelperMethods {

  /** If this method fails, it means that the code provided as a starting point no longer works correctly. */
  private def sanityCheck(): Unit = {
    val line = "before we proceed - before we proceed, hear me speak."
    assertSameElements(
      actual = TestNGrams.splitIntoNGrams(line),
      expected = List(
        ("before", "we", "proceed"),
        ("we", "proceed", "before"),
        ("proceed", "before", "we"),
        ("before", "we", "proceed"),
        ("we", "proceed", "hear"),
        ("proceed", "hear", "me"),
        ("hear", "me", "speak"),
      )
    )
  }

  test("'splitIntoNGrams' should lowercase all words in the sentence (1pts)") {
    sanityCheck()
    val line = "Hear CLAUDIUS speak!"
    assertSameElements(
      actual = TestNGrams.splitIntoNGrams(line),
      expected = List(("hear", "claudius", "speak")),
    )
  }

  test("'splitIntoNGrams' should pad ngrams with empty strings (1pts)") {
    sanityCheck()
    assertSameElements(
      actual = TestNGrams.splitIntoNGrams("no."),
      expected = List(("no", "", "")),
    )

    assertSameElements(
      actual = TestNGrams.splitIntoNGrams("not yet"),
      expected = List(("not", "yet", ""))
    )
  }

  // NOTE: you will be graded based on the result of running your code on the entire corpus
  test("'aggregateNGrams' should correctly aggregate n-grams (unit test) (1pts)") {
    val testLines = Seq(
      "Before we proceed, hear me speak.",
      "Before we proceed.",
      "Hear me speak.",
      "Art thou waking?",
    )

    def aggregateNGrams(lines: Seq[String]) = {
      import TestNGrams.{lines => _, *}
      createNGramsRDD(lines)
        .aggregate(aggregateNGrams_zero)(aggregateNGrams_seqOp, aggregateNGrams_combOp)
    }

    assertSameElements(
      actual = aggregateNGrams(testLines).toList,
      expected = List(
        ("before", "we", "proceed") -> 2,
        ("art", "thou", "waking") -> 1,
        ("hear", "me", "speak") -> 2,
        ("proceed", "hear", "me") -> 1,
        ("we", "proceed", "hear") -> 1,
      )
    )
  }

  test("'createSpeakerLinesRDD' should correctly split speakers and lines (1pts)") {
    val testLines = List(
      "First Citizen: Before we proceed, hear me speak.",
      "First Citizen: Before we proceed: hear me speak.",
      "First Citizen: Before we proceed.",
      "Second Citizen: Hear me speak.",
      "SEBASTIAN: Art thou waking?",
      "ANTONIO: Do you not hear me speak?",
    )
    val expected = List(
      "First Citizen" -> "Before we proceed, hear me speak.",
      "First Citizen" -> "Before we proceed: hear me speak.",
      "First Citizen" -> "Before we proceed.",
      "Second Citizen" -> "Hear me speak.",
      "SEBASTIAN" -> "Art thou waking?",
      "ANTONIO" -> "Do you not hear me speak?",
    )
    assertSameElements(
      actual = TestNGrams.createSpeakerLinesRDD(testLines).toLocalIterator.map {
        (k, v) => k.trim -> v.trim
      }.toList,
      expected = expected,
    )
  }

  // NOTE: you will be graded based on the result of running your code on the entire corpus
  test("Speaker-specific n-grams should be correctly calculated (unit test) (1pts)") {
    val testLines = List(
      "First Citizen: Before we proceed, hear me speak.",
      "First Citizen: Before we proceed.",
      "Second Citizen: Hear me speak.",
      "SEBASTIAN: Art thou waking?",
      "ANTONIO: Do you not hear me speak?",
    )

    def speakerNGramsIter(lines: Seq[String]) = {
      import TestNGrams.{lines => _, *}
      createSpeakerNGramsRDD(createSpeakerLineNGramsRDD(createSpeakerLinesRDD(lines)))
        .toLocalIterator
    }

    assertSameElements(
      actual = speakerNGramsIter(testLines).toList,
      expected = List(
        ("SEBASTIAN", Map(("art","thou","waking") -> 1)),
        ("ANTONIO", Map(("do", "you", "not") -> 1, ("you", "not", "hear") -> 1, ("not", "hear", "me") -> 1, ("hear", "me", "speak") -> 1)),
        ("First Citizen", Map(("before", "we", "proceed") -> 2, ("we", "proceed", "hear") -> 1, ("proceed", "hear", "me") -> 1, ("hear", "me", "speak") -> 1)),
        ("Second Citizen", Map(("hear", "me", "speak") -> 1))
      )
    )
  }

    }

trait HelperMethods {
  /**
    * Creates a truncated string representation of a list, adding ", ...)" if there
    * are too many elements to show
    * @param l The list to preview
    * @param n The number of elements to cut it at
    * @return A preview of the list, containing at most n elements.
    */
  def previewList[A](l: List[A], n: Int = 10): String =
    if (l.length <= n) l.toString
    else l.take(n).toString.dropRight(1) + ", ...)"

  /**
    * Asserts that all the elements in a given list and an expected list are the same,
    * regardless of order. For a prettier output, given and expected should be sorted
    * with the same ordering.
    * @param actual The actual list
    * @param expected The expected list
    * @tparam A Type of the list elements
    */
  def assertSameElements[A](actual: List[A], expected: List[A]): Unit = {
    val givenSet = actual.toSet
    val expectedSet = expected.toSet

    val unexpected = givenSet -- expectedSet
    val missing = expectedSet -- givenSet

    val noUnexpectedElements = unexpected.isEmpty
    val noMissingElements = missing.isEmpty

    val noMatchString =
      s"""
         |Expected: ${previewList(expected)}
         |Actual:   ${previewList(actual)}""".stripMargin

    assert(noUnexpectedElements,
      s"""|$noMatchString
          |The given collection contains some unexpected elements: ${previewList(unexpected.toList, 5)}""".stripMargin)

    assert(noMissingElements,
      s"""|$noMatchString
          |The given collection is missing some expected elements: ${previewList(missing.toList, 5)}""".stripMargin)
  }

  // Conditions:
  // (1) the language stats contain the same elements
  // (2) they are ordered (and the order doesn't matter if there are several languages with the same count)
  def assertEquivalentAndOrdered(actual: List[(String, Int)], expected: List[(String, Int)]): Unit = {
    // (1)
    assertSameElements(actual, expected)
    // (2)
    assert(
      !(actual zip actual.tail).exists({ case ((_, occ1), (_, occ2)) => occ1 < occ2 }),
      "The given elements are not in descending order"
    )
  }
}

object TestNGrams extends NGrams {
  import org.apache.spark.SparkConf
  import org.apache.spark.SparkContext
  import org.apache.spark.SparkContext._
  import org.apache.log4j.{Logger, Level}

  import org.apache.spark.rdd.RDD

  import scala.util.Properties.isWin

  // Reduce Spark logging verbosity
  Logger.getLogger("org").setLevel(Level.ERROR)

  if (isWin) System.setProperty("hadoop.home.dir", System.getProperty("user.dir") + "\\winutils\\hadoop-2.7.4")

  val conf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("NGram")

  val sc: SparkContext = new SparkContext(conf)
  val lines = F4TestData.lines
}

object F4TestData {
  import scala.io.Source
  import scala.io.Codec

  val punctuationRx = "[,:;?!.'\"]+".r

  def linesIterator: Iterator[String] = {
    Option(getClass.getResourceAsStream("/f4/shakespeare.txt")) match {
      case Some(resource) =>
        DoubleLineIterator(Source.fromInputStream(resource)(Codec.UTF8))
      case None =>
        throw new RuntimeException("The resource with the corpus is unexpectedly missing, please inform the staff.")
    }
  }

  def lines = linesIterator.toSeq
}

class DoubleLineIterator(underlying: Iterator[Char]) extends scala.collection.AbstractIterator[String] with Iterator[String] {
  private[this] val sb = new StringBuilder

  lazy val iter: BufferedIterator[Char] = underlying.buffered

  def getc(): Boolean = iter.hasNext && {
    val ch = iter.next()
    if (ch == '\n') {
      val has = iter.hasNext
      val ch2 = if has then iter.next() else ' '
      if (has && ch2 == '\n')
        false
      else {
        sb append ' '
        sb append ch2
        true
      }
    } else {
      sb append ch
      true
    }
  }
  def hasNext: Boolean = iter.hasNext
  def next(): String = {
    sb.clear()
    while (getc()) { }
    sb.toString
  }
}

