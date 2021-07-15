package m7

import java.util.concurrent._
import scala.util.DynamicVariable

class M7Suite extends AbstractM7Suite {

  // (70+) 30 / 500 points for correct implementation, don't check parallelism
  test("[Correctness] fetch result - simple combiners (5pts)") {
    assertCorrectnessSimple()
  }

  test("[Correctness] fetch result - small combiners (5pts)") {
    assertCorrectnessBasic()
  }

  test("[Correctness] fetch result - small combiners after combining (10pts)") {
    assertCorrectnessCombined()
  }

  test("[Correctness] fetch result - large combiners (10pts)") {
    assertCorrectnessLarge()
  }

  def assertCorrectnessSimple() = {
    simpleCombiners.foreach(elem => assert(compare(elem._1, elem._2)))
  }

  def assertCorrectnessBasic() = {
    basicCombiners.foreach(elem => assert(compare(elem._1, elem._2)))
  }

  def assertCorrectnessCombined() = {
    combinedCombiners.foreach(elem => assert(compare(elem._1, elem._2)))
  }

  def assertCorrectnessLarge() = {
    largeCombiners.foreach(elem => assert(compare(elem._1, elem._2)))
  }

  // (70+30+) 50 / 500 points for correct parallel implementation, don't check if it's exactly 1/4 of the array per task
  private var count = 0
  private val expected = 3

  override def task[T](body: => T): ForkJoinTask[T] = {
    count += 1
    scheduler.value.schedule(body)
  }

  test("[TaskCount] number of newly created tasks should be 3 (5pts)") {
    assertTaskCountSimple()
  }

  test("[TaskCount] fetch result and check parallel - simple combiners (5pts)") {
    assertTaskCountSimple()
    assertCorrectnessSimple()
  }

  test("[TaskCount] fetch result and check parallel - small combiners (10pts)") {
    assertTaskCountSimple()
    assertCorrectnessBasic()
  }

  test("[TaskCount] fetch result and check parallel - small combiners after combining (15pts)") {
    assertTaskCountSimple()
    assertCorrectnessCombined()
  }

  test("[TaskCount] fetch result and check parallel - large combiners (15pts)") {
    assertTaskCountSimple()
    assertCorrectnessLarge()
  }

  def assertTaskCountSimple(): Unit = {
    simpleCombiners.foreach(elem => assertTaskCount(elem._1, elem._2))
  }

  def assertTaskCount(combiner: DLLCombinerTest, array: Array[Int]): Unit = {
    try {
      count = 0
      build(combiner, array)
      combiner.result()
      assertEquals(count, expected, {
        s"ERROR: Expected $expected instead of $count calls to `task(...)`"
      })
    } finally {
      count = 0
    }
  }

  //(70+30+50+) 200 / 500 points for correct parallel implementation, exactly 1/4 of the array per task
  test("[TaskFairness] each task should compute 1/4 of the result (50pts)") {
    assertTaskFairness(simpleCombiners.unzip._1)
  }

  test("[TaskFairness] each task should correctly compute 1/4 of the result - simple combiners (20pts)") {
    assertTaskFairness(simpleCombiners.unzip._1)
    assertCorrectnessSimple()
  }

  test("[TaskFairness] each task should correctly compute 1/4 of the result - small combiners (30pts)") {
    assertTaskFairness(basicCombiners.unzip._1)
    assertCorrectnessBasic()
  }

  test("[TaskFairness] each task should correctly compute 1/4 of the result - small combiners after combining (50pts)") {
    assertTaskFairness(combinedCombiners.unzip._1)
    assertCorrectnessCombined()
  }

  test("[TaskFairness] each task should correctly compute 1/4 of the result - large combiners (50pts)") {
    assertTaskFairness(largeCombiners.unzip._1)
    assertCorrectnessLarge()
  }

  def assertTaskFairness(combiners: List[DLLCombinerTest]): Unit = {
    def assertNewTaskFairness(combiner: DLLCombinerTest, task: ForkJoinTask[Unit], data: Array[Int]) = {
      var count = 0
      var expected = combiner.cnt / 4
      task.join
      count = data.count(elem => elem != 0)

      assert((count - expected).abs <= 1)
    }

    def assertMainTaskFairness(combiner: DLLCombinerTest, task: Unit, data: Array[Int]) = {
      var count = 0
      var expected = combiner.cnt / 4
      count = data.count(elem => elem != 0)

      assert((count - expected).abs <= 1)
    }

    combiners.foreach { elem =>
      var data = Array.fill(elem.cnt)(0)
      assertNewTaskFairness(elem, elem.task1(data), data)

      data = Array.fill(elem.cnt)(0)
      assertNewTaskFairness(elem, elem.task2(data), data)

      data = Array.fill(elem.cnt)(0)
      assertNewTaskFairness(elem, elem.task3(data), data)

      data = Array.fill(elem.cnt)(0)
      assertMainTaskFairness(elem, elem.task4(data), data)
    }
  }

  //(70+30+50+200+) 150 / 500 points for correct parallel implementation, exactly 1/4 of the array per task, exactly the specified quarter

  test("[TaskPrecision] each task should compute specified 1/4 of the result - simple combiners (20pts)") {
    assertTaskPrecision(simpleCombiners)
  }

  test("[TaskPrecision] each task should compute specified 1/4 of the result - small combiners (30pts)") {
    assertTaskPrecision(basicCombiners)
  }

  test("[TaskPrecision] each task should compute specified 1/4 of the result - small combiners after combining (50pts)") {
    assertTaskPrecision(combinedCombiners)
  }

  test("[TaskPrecision] each task should compute specified 1/4 of the result - large combiners (50pts)") {
    assertTaskPrecision(largeCombiners)
  }

  def assertTaskPrecision(combiners: List[(DLLCombinerTest, Array[Int])]): Unit = {
    combiners.foreach { elem =>
      var data = Array.fill(elem._1.cnt)(0)
      var ref = Array.fill(elem._1.cnt)(0)
      val task1 = elem._1.task1(data)
      task1.join
      Range(0, elem._1.cnt).foreach(i => (if (i < elem._1.cnt / 2 - 1 && i % 2 == 1) ref(i) = elem._2(i)))
      assert(Range(0, elem._1.cnt / 2 - 1).forall(i => data(i) == ref(i)))

      data = Array.fill(elem._1.cnt)(0)
      ref = Array.fill(elem._1.cnt)(0)
      val task2 = elem._1.task2(data)
      task2.join
      Range(0, elem._1.cnt).foreach(i => (if (i < elem._1.cnt / 2 - 1 && i % 2 == 0) ref(i) = elem._2(i)))
      assert(Range(0, elem._1.cnt / 2 - 1).forall(i => data(i) == ref(i)))

      data = Array.fill(elem._1.cnt)(0)
      ref = Array.fill(elem._1.cnt)(0)
      val task3 = elem._1.task3(data)
      task3.join
      Range(0, elem._1.cnt).foreach(i => (if (i > elem._1.cnt / 2 + 1 && i % 2 != elem._1.cnt % 2) ref(i) = elem._2(i)))
      assert(Range(elem._1.cnt / 2 + 2, elem._1.cnt).forall(i => data(i) == ref(i)))

      data = Array.fill(elem._1.cnt)(0)
      ref = Array.fill(elem._1.cnt)(0)
      val task4 = elem._1.task4(data)
      Range(0, elem._1.cnt).foreach(i => (if (i > elem._1.cnt / 2 + 1 && i % 2 == elem._1.cnt % 2) ref(i) = elem._2(i)))
      assert(Range(elem._1.cnt / 2 + 2, elem._1.cnt).forall(i => data(i) == ref(i)))
    }
  }

  test("[Public] fetch simple result without combining (5pts)") {
    val combiner1 = DLLCombinerTest(2)
    combiner1 += 7
    combiner1 += 2
    combiner1 += 3
    combiner1 += 8
    combiner1 += 1
    combiner1 += 2
    combiner1 += 3
    combiner1 += 8

    val result = combiner1.result()
    val array = Array(7, 2, 3, 8, 1, 2, 3, 8)

    assert(Range(0,array.size).forall(i => array(i) == result(i)))
  }

  test("[Public] fetch result without combining (5pts)") {
    val combiner1 = DLLCombinerTest(2)
    combiner1 += 7
    combiner1 += 2
    combiner1 += 3
    combiner1 += 8
    combiner1 += 1

    val result = combiner1.result()
    val array = Array(7, 2, 3, 8, 1)

    assert(Range(0,array.size).forall(i => array(i) == result(i)))
  }

  test("[Public] fetch result after simple combining (5pts)") {
    val combiner1 = DLLCombinerTest(2)
    combiner1 += 7
    combiner1 += 2

    val combiner2 = DLLCombinerTest(2)
    combiner2 += 3
    combiner2 += 8

    val combiner3 = DLLCombinerTest(2)
    combiner2 += 1
    combiner2 += 9

    val combiner4 = DLLCombinerTest(2)
    combiner2 += 3
    combiner2 += 2

    val result = combiner1.combine(combiner2).combine(combiner3).combine(combiner4).result()
    val array = Array(7, 2, 3, 8, 1, 9, 3, 2)

    assert(Range(0,array.size).forall(i => array(i) == result(i)))
  }

  test("[Public] fetch result - empty combiner (20pts)") {
    val combiner1 = DLLCombinerTest(2)
    val result = combiner1.result()
    assertEquals(result.size, 0)
  }

  test("[Public] fetch result - full single element combiner (15pts)") {
    val combiner1 = DLLCombinerTest(3)
    combiner1 += 4
    combiner1 += 2
    combiner1 += 6

    val result = combiner1.result()
    val array = Array(4, 2, 6)

    assert(Range(0,array.size).forall(i => array(i) == result(i)))
  }

}


trait AbstractM7Suite extends munit.FunSuite with LibImpl {

  def simpleCombiners = buildSimpleCombiners()
  def basicCombiners = buildBasicCombiners()
  def combinedCombiners = buildCombinedCombiners()
  def largeCombiners = buildLargeCombiners()

  def buildSimpleCombiners() = {
    val simpleCombiners = List(
      (DLLCombinerTest(4), Array(4, 2, 6, 1, 5, 4, 3, 5, 6, 3, 4, 5, 6, 3, 4, 5)),
      (DLLCombinerTest(4), Array(7, 2, 2, 9, 3, 2, 1, 1, 1, 1, 1, 1, 1, 2, 3, 2)),
      (DLLCombinerTest(4), Array.fill(16)(5))
    )
    simpleCombiners.foreach(elem => build(elem._1, elem._2))
    simpleCombiners
  }

  def buildBasicCombiners() = {
    val basicCombiners = List(
      (DLLCombinerTest(2), Array(4, 2, 6)),
      (DLLCombinerTest(5), Array(4, 2, 6)),
      (DLLCombinerTest(3), Array(4, 2, 6, 1, 7, 2, 4)),
      (DLLCombinerTest(4), Array(7, 2, 2, 9, 3, 2, 11, 12, 13, 14, 15, 16, 17, 22)),
      (DLLCombinerTest(3), Array.fill(16)(7)),
      (DLLCombinerTest(3), Array.fill(19)(7)),
      (DLLCombinerTest(3), Array.fill(5)(7)),
      (DLLCombinerTest(3), Array.fill(6)(7))
    )
    basicCombiners.foreach(elem => build(elem._1, elem._2))
    basicCombiners
  }

  def buildCombinedCombiners() = {
    var combinedCombiners = List[(DLLCombinerTest, Array[Int])]()
    Range(1, 10).foreach { chunk_size =>
      val array = basicCombiners.filter(elem => elem._1.chunk_size == chunk_size).foldLeft(Array[Int]()) { (acc, i) => acc ++ i._2 }
      val combiner = DLLCombinerTest(chunk_size)
      basicCombiners.filter(elem => elem._1.chunk_size == chunk_size).foreach(elem => combiner.combine(elem._1))

      combinedCombiners  = combinedCombiners :+ (combiner, array)
    }
    combinedCombiners
  }

  def buildLargeCombiners() = {
    val largeCombiners = List(
      (DLLCombinerTest(21), Array.fill(1321)(4) ++ Array.fill(1322)(7)),
      (DLLCombinerTest(18), Array.fill(1341)(2) ++ Array.fill(1122)(5)),
      (DLLCombinerTest(3), Array.fill(1321)(4) ++ Array.fill(1322)(7) ++ Array.fill(321)(4) ++ Array.fill(322)(7)),
      (DLLCombinerTest(12), Array.fill(992321)(4) ++ Array.fill(99322)(7)),
      (DLLCombinerTest(4), Array.fill(953211)(4) ++ Array.fill(999322)(1))
    )
    largeCombiners.foreach(elem => build(elem._1, elem._2))
    largeCombiners
  }

  def build(combiner: DLLCombinerTest, array: Array[Int]): DLLCombinerTest = {
    array.foreach(elem => combiner += elem)
    combiner
  }

  def compare(combiner: DLLCombinerTest, array: Array[Int]): Boolean = {
    val result = combiner.result()
    Range(0,array.size).forall(i => array(i) == result(i))
  }

  def buildAndCompare(combiner: DLLCombinerTest, array: Array[Int]): Boolean = {
    array.foreach(elem => combiner += elem)
    val result = combiner.result()

    Range(0,array.size).forall(i => array(i) == result(i))
  }

}

trait LibImpl extends M7 {

  val forkJoinPool = new ForkJoinPool

  abstract class TaskScheduler {
    def schedule[T](body: => T): ForkJoinTask[T]
  }

  class DefaultTaskScheduler extends TaskScheduler {
    def schedule[T](body: => T): ForkJoinTask[T] = {
      val t = new RecursiveTask[T] {
        def compute = body
      }
      Thread.currentThread match {
        case wt: ForkJoinWorkerThread =>
          t.fork()
        case _ =>
          forkJoinPool.execute(t)
      }
      t
    }
  }

  val scheduler =
    new DynamicVariable[TaskScheduler](new DefaultTaskScheduler)

  def task[T](body: => T): ForkJoinTask[T] = {
    scheduler.value.schedule(body)
  }

  class DLLCombinerTest(chunk_size: Int = 3) extends DLLCombinerImplementation(chunk_size) {

    override def +=(elem: Int): Unit = {
      if(cnt % chunk_size == 0) {
        chunks = chunks + 1
        val node = new Node(chunk_size)
        if (cnt == 0) {
          head = node
          last = node
        }
        else {
          last.next = node
          node.previous = last
          last = node
        }
      }
      last.add(elem)
      cnt += 1
    }

    override def combine(that: DLLCombiner): DLLCombiner = {
      assert(this.chunk_size == that.chunk_size)
      if (this.cnt == 0) {
        this.head = that.head
        this.last = that.last
        this.cnt = that.cnt
        this.chunks = that.chunks

        this
      }
      else if (that.cnt == 0)
        this
      else {
        this.last.next = that.head
        that.head.previous = this.last

        this.cnt = this.cnt + that.cnt
        this.chunks = this.chunks + that.chunks
        this.last = that.last

        this
      }
    }
  }
}
