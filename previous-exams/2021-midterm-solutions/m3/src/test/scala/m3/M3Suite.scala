package m3

class M3Suite extends munit.FunSuite {

  test("Rolling windowed average result test (5pts)") {
    RollingWinMeanBasicLogicTest.basicTests()
    RollingWinMeanBasicLogicTest.normalTests()
    RollingWinMeanBasicLogicTest.largeTests()
  }

  test("[TASK 1] Rolling windowed average parallelism test (30pts)") {
    RollingWinMeanCallsToParallel.parallelismTest()
    RollingWinMeanParallel.basicTests()
    RollingWinMeanParallel.normalTests()
    RollingWinMeanParallel.largeTests()
  }

  test("[TASK 2] Rolling windowed average no `map` test (35pts)") {
    RollingWinMeanNoMap.basicTests()
    RollingWinMeanNoMap.normalTests()
    RollingWinMeanNoMap.largeTests()
  }

  test("[TASK 3] Rolling windowed average no `tail` test (30pts)") {
    RollingWinMeanNoTail.basicTests()
    RollingWinMeanNoTail.normalTests()
    RollingWinMeanNoTail.largeTests()
  }


  object RollingWinMeanBasicLogicTest extends M3 with LibImpl with RollingWinMeanTest {
    def parallel[T1, T2](op1: => T1, op2: => T2): (T1, T2) = (op1, op2)
    def newArrFrom[T](arr: Array[AnyRef]): Arr[T] = new ArrImpl(arr)
  }

  object RollingWinMeanCallsToParallel extends M3 with LibImpl with RollingWinMeanTest {
    private var count = 0
    def parallel[T1, T2](op1: => T1, op2: => T2): (T1, T2) =
      count += 1
      (op1, op2)

    def newArrFrom[T](arr: Array[AnyRef]): Arr[T] = new ArrImpl(arr)

    def parallelismTest() = {
      assertParallelCount(Arr(), 0)
      assertParallelCount(Arr(1), 0)
      assertParallelCount(Arr(1, 2, 3, 4), 0)
      assertParallelCount(Arr(Array.tabulate(16)(identity): _*), 0)
      assertParallelCount(Arr(Array.tabulate(32)(identity): _*), 0)

      assertParallelCount(Arr(Array.tabulate(33)(identity): _*), 2)
      assertParallelCount(Arr(Array.tabulate(64)(identity): _*), 2)
      assertParallelCount(Arr(Array.tabulate(128)(identity): _*), 6)
      assertParallelCount(Arr(Array.tabulate(256)(identity): _*), 14)
      assertParallelCount(Arr(Array.tabulate(1000)(identity): _*), 62)
      assertParallelCount(Arr(Array.tabulate(1024)(identity): _*), 62)
    }

    def assertParallelCount(arr: Arr[Int], expected: Int): Unit = {
      try {
        count = 0
        rollingWinMeanParallel(arr)
        assert(count == expected, {
          val extra = if (expected == 0) "" else s" ${expected/2} for the `upsweep` and ${expected/2} for the `downsweep`"
          s"\n$arr\n\nERROR: Expected $expected instead of $count calls to `parallel(...)` for an array of ${arr.length} elements. Current parallel threshold is $THRESHOLD.$extra"
        })
      } finally {
        count = 0
      }
    }

  }

  object RollingWinMeanNoMap extends M3 with LibImpl with RollingWinMeanTest {
    def parallel[T1, T2](op1: => T1, op2: => T2): (T1, T2) = (op1, op2)
    def newArrFrom[T](arr: Array[AnyRef]): Arr[T] = new ArrImpl[T](arr) {
      override def map[U](f: T => U): Arr[U] = throw Exception("Should not call Arr.map")
    }
  }

  object RollingWinMeanNoTail extends M3 with LibImpl with RollingWinMeanTest {
    def parallel[T1, T2](op1: => T1, op2: => T2): (T1, T2) = (op1, op2)
    def newArrFrom[T](arr: Array[AnyRef]): Arr[T] = new ArrImpl[T](arr) {
      override def tail: Arr[T] = throw Exception("Should not call Arr.tail")
    }
  }

  object RollingWinMeanParallel extends M3 with LibImpl with RollingWinMeanTest {
    import scala.concurrent.duration._
    val TIMEOUT = Duration(10, SECONDS)
    def parallel[T1, T2](op1: => T1, op2: => T2): (T1, T2) = {
      import concurrent.ExecutionContext.Implicits.global
      import scala.concurrent._
      Await.result(Future(op1).zip(Future(op2)), TIMEOUT) // FIXME not timing-out
    }
    def newArrFrom[T](arr: Array[AnyRef]): Arr[T] = new ArrImpl(arr)
  }

  trait LibImpl extends Lib {

    def newArrFrom[T](arr: Array[AnyRef]): Arr[T]

    def newArrOfLength[T](n: Int): Arr[T] =
      newArrFrom(new Array(n))

    class ArrImpl[T](val arr: Array[AnyRef]) extends Arr[T]:
      def apply(i: Int): T =
        arr(i).asInstanceOf[T]
      def update(i: Int, x: T): Unit =
        arr(i) = x.asInstanceOf[AnyRef]
      def length: Int =
        arr.length
      def map[U](f: T => U): Arr[U] =
        newArrFrom(arr.map(f.asInstanceOf[AnyRef => AnyRef]))
      def tail: Arr[T] =
        newArrFrom(arr.tail)
      override def toString: String =
        arr.mkString("Arr(", ", ", ")")
      override def equals(that: Any): Boolean =
        that match
          case that: ArrImpl[_] => Array.equals(arr, that.arr)
          case _ => false
  }

  trait RollingWinMeanTest extends M3 {

    def tabulate[T](n: Int)(f: Int => T): Arr[T] =
      val arr = Arr.ofLength[T](n)
      for i <- 0 until n do
        arr(i) = f(i)
      arr

    def asSeq(arr: Arr[Double]) =
      val array = new Array[Double](arr.length)
      for i <- 0 to (arr.length - 1) do
        array(i) = arr(i)
      array.toSeq

    def scanOp_(acc: AvgWin, x: AvgWin) =
      acc.pushAll(x)

    def result(ds: Seq[Int]): Arr[Double] =
      Arr(ds.map(x => AvgWin(x :: Nil)).scan(AvgWin(Nil))(scanOp_).tail.map(_.toDouble): _*)

    def check(input: Seq[Int]) =
      assertEquals(
        asSeq(rollingWinMeanParallel(Arr(input: _*))),
        asSeq(result(input))
      )

    def basicTests() = {
      check(Seq())
      check(Seq(1))
      check(Seq(1, 2, 3, 4))
      check(Seq(4, 4, 4, 4))
    }

    def normalTests() = {
      check(Seq.tabulate(64)(identity))
      check(Seq(4, 4, 4, 4))
      check(Seq(4, 8, 6, 4))
      check(Seq(4, 3, 2, 1))
      check(Seq.tabulate(64)(identity).reverse)
      check(Seq.tabulate(128)(i => 128 - 2*i).reverse)
    }

    def largeTests() = {
      check(Seq.tabulate(500)(identity))
      check(Seq.tabulate(512)(identity))
      check(Seq.tabulate(1_000)(identity))
      check(Seq.tabulate(10_000)(identity))
    }
  }
}
