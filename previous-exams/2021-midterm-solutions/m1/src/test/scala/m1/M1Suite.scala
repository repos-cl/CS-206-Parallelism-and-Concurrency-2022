package m1

class M1Suite extends munit.FunSuite {

  test("Rolling average result test (5pts)") {
    RollingAveragesBasicLogicTest.basicTests()
    RollingAveragesBasicLogicTest.normalTests()
    RollingAveragesBasicLogicTest.largeTests()
  }

  test("[TASK 1] Rolling average parallelism test (30pts)") {
    RollingAveragesCallsToParallel.parallelismTest()
    RollingAveragesParallel.basicTests()
    RollingAveragesParallel.normalTests()
    RollingAveragesParallel.largeTests()
  }

  test("[TASK 2] Rolling average no `map` test (35pts)") {
    RollingAveragesNoMap.basicTests()
    RollingAveragesNoMap.normalTests()
    RollingAveragesNoMap.largeTests()
  }

  test("[TASK 3] Rolling average no `tail` test (30pts)") {
    RollingAveragesNoTail.basicTests()
    RollingAveragesNoTail.normalTests()
    RollingAveragesNoTail.largeTests()
  }


  object RollingAveragesBasicLogicTest extends M1 with LibImpl with RollingAveragesTest {
    def parallel[T1, T2](op1: => T1, op2: => T2): (T1, T2) = (op1, op2)
    def newArrFrom[T](arr: Array[AnyRef]): Arr[T] = new ArrImpl(arr)
  }

  object RollingAveragesCallsToParallel extends M1 with LibImpl with RollingAveragesTest {
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
        rollingAveragesParallel(arr)
        assert(count == expected, {
          val extra = if (expected == 0) "" else s" ${expected/2} for the `upsweep` and ${expected/2} for the `downsweep`"
          s"\n$arr\n\nERROR: Expected $expected instead of $count calls to `parallel(...)` for an array of ${arr.length} elements. Current parallel threshold is $THRESHOLD.$extra"
        })
      } finally {
        count = 0
      }
    }

  }

  object RollingAveragesNoMap extends M1 with LibImpl with RollingAveragesTest {
    def parallel[T1, T2](op1: => T1, op2: => T2): (T1, T2) = (op1, op2)
    def newArrFrom[T](arr: Array[AnyRef]): Arr[T] = new ArrImpl[T](arr) {
      override def map[U](f: T => U): Arr[U] = throw Exception("Should not call Arr.map")
    }
  }

  object RollingAveragesNoTail extends M1 with LibImpl with RollingAveragesTest {
    def parallel[T1, T2](op1: => T1, op2: => T2): (T1, T2) = (op1, op2)
    def newArrFrom[T](arr: Array[AnyRef]): Arr[T] = new ArrImpl[T](arr) {
      override def tail: Arr[T] = throw Exception("Should not call Arr.tail")
    }
  }

  object RollingAveragesParallel extends M1 with LibImpl with RollingAveragesTest {
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

  trait RollingAveragesTest extends M1 {

    def tabulate[T](n: Int)(f: Int => T): Arr[T] =
      val arr = Arr.ofLength[T](n)
      for i <- 0 until n do
        arr(i) = f(i)
      arr

    def basicTests() = {
      assertEquals(rollingAveragesParallel(Arr()), Arr[Double]())
      assertEquals(rollingAveragesParallel(Arr(1)), Arr[Double](1))
      assertEquals(rollingAveragesParallel(Arr(1, 2, 3, 4)), Arr(1, 1.5, 2, 2.5))
      assertEquals(rollingAveragesParallel(Arr(4, 4, 4, 4)), Arr[Double](4, 4, 4, 4))
    }

    def normalTests() = {
      assertEquals(rollingAveragesParallel(Arr(Array.tabulate(64)(identity): _*)), Arr(Array.tabulate(64)(_.toDouble / 2): _*))
      assertEquals(rollingAveragesParallel(Arr(4, 4, 4, 4)), Arr[Double](4, 4, 4, 4))
      assertEquals(rollingAveragesParallel(Arr(4, 8, 6, 4)), Arr[Double](4, 6, 6, 5.5))
      assertEquals(rollingAveragesParallel(Arr(4, 3, 2, 1)), Arr(4, 3.5, 3, 2.5))
      assertEquals(rollingAveragesParallel(Arr(Array.tabulate(64)(identity).reverse: _*)), Arr(Array.tabulate(64)(i => 63 - i.toDouble / 2): _*))
      assertEquals(rollingAveragesParallel(Arr(Array.tabulate(128)(i => 128 - 2*i).reverse: _*)), Arr(Array.tabulate(128)(i => -126d + i): _*))
    }

    def largeTests() = {
      assertEquals(rollingAveragesParallel(Arr(Array.tabulate(500)(identity): _*)), Arr(Array.tabulate(500)(_.toDouble / 2): _*))
      assertEquals(rollingAveragesParallel(Arr(Array.tabulate(512)(identity): _*)), Arr(Array.tabulate(512)(_.toDouble / 2): _*))
      assertEquals(rollingAveragesParallel(Arr(Array.tabulate(1_000)(identity): _*)), Arr(Array.tabulate(1_000)(_.toDouble / 2): _*))
      assertEquals(rollingAveragesParallel(Arr(Array.tabulate(10_000)(identity): _*)), Arr(Array.tabulate(10_000)(_.toDouble / 2): _*))
    }
  }
}