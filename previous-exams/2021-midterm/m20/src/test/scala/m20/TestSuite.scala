package m20

import scala.concurrent._
import scala.concurrent.duration._
import scala.collection.mutable.HashMap
import scala.util.Random
import instrumentation._
import instrumentation.TestHelper._
import instrumentation.TestUtils._

enum ThreadResult:
  case WriteError(error: String)
  case WriteSuccess
  case Read(result: (Int, Int))
import ThreadResult._

class M20Suite extends munit.FunSuite:
  /** If at least one thread resulted in an error,
   *  return `(false, errorMessage)` otherwise return `(true, "")`.
   */
  def processResults(results: List[ThreadResult]): (Boolean, String) =
    val success = (true, "")
    results.foldLeft(success) {
      case (acc @ (false, _), _) =>
        // Report the first error found
        acc
      case (_, WriteError(error)) =>
        (false, error)
      case (_, Read((x, y))) if x + 1 != y =>
        (false, s"Read ($x, $y) but expected y to be ${x + 1}")
      case (_, _: Read | WriteSuccess) =>
        success
    }

  def randomList(length: Int): List[Int] =
    List.fill(length)(Random.nextInt)

  test("SeqCount: single-threaded write and copy (1 pts)") {
    val sc = new SeqCount
    randomList(100).lazyZip(randomList(100)).foreach { (x, y) =>
      sc.write(x, y)
      assertEquals(sc.copy(), (x, y))
    }
  }

  test("SeqCount: one write thread, two copy threads (4 pts)") {
    testManySchedules(3, sched =>
      val sc = new SchedulableSeqCount(sched)
      // Invariant in this test: y == x + 1
      sc.write(0, 1)

      val randomValues = randomList(length = 5)

      def writeThread(): ThreadResult =
        randomValues.foldLeft(WriteSuccess) {
          case (res: WriteError, _) =>
            // Report the first error found
            res
          case (_, i) =>
            sc.write(i, i + 1)
            val writtenValues = (i, i + 1)
            val readBack = sc.copy()
            if writtenValues != readBack then
              WriteError(s"Wrote $writtenValues but read back $readBack")
            else
              WriteSuccess
        }

      def copyThread(): ThreadResult =
        Read(sc.copy())

      val threads = List(
        () => writeThread(),
        () => copyThread(),
        () => copyThread()
      )

      (threads, results => processResults(results.asInstanceOf[List[ThreadResult]]))
    )
  }

  test("MultiWriterSeqCount: single-threaded write and copy (1 pts)") {
    val sc = new MultiWriterSeqCount
    randomList(100).lazyZip(randomList(100)).foreach { (x, y) =>
      sc.write(x, y)
      assertEquals(sc.copy(), (x, y))
    }
  }

  test("MultiWriterSeqCount: two write threads, two copy threads (4 pts)") {
    testManySchedules(4, sched =>
      val msc = new SchedulableMultiWriterSeqCount(sched)
      // Invariant in this test: y == x + 1
      msc.write(0, 1)

      val randomValues = randomList(length = 5)

      def writeThread(): ThreadResult =
        randomValues.foreach(i => msc.write(i, i + 1))
        // Unlke in the SeqCount test, we do not verify that we can read back
        // the values we wrote, because the other writer thread might have
        // overwritten them already.
        WriteSuccess

      def copyThread(): ThreadResult =
        Read(msc.copy())

      val threads = List(
        () => writeThread(),
        () => writeThread(),
        () => copyThread(),
        () => copyThread()
      )

      (threads, results => processResults(results.asInstanceOf[List[ThreadResult]]))
    )
  }

  import scala.concurrent.duration._
  override val munitTimeout = 200.seconds
end M20Suite

