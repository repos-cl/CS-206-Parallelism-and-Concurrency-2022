package concpar22final01

import java.util.concurrent.*
import scala.util.DynamicVariable

class Problem1Suite extends AbstractProblem1Suite:

  test("[Public] fetch simple result without combining (2pts)") {
    val combiner1 = new DLLCombinerTest
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

    assert(Range(0, array.size).forall(i => array(i) == result(i)))
  }

  test("[Public] fetch result without combining (2pts)") {
    val combiner1 = new DLLCombinerTest
    combiner1 += 7
    combiner1 += 2
    combiner1 += 3
    combiner1 += 8
    combiner1 += 1

    val result = combiner1.result()
    val array = Array(7, 2, 3, 8, 1)

    assert(Range(0, array.size).forall(i => array(i) == result(i)))
  }

  test("[Public] fetch result after simple combining (2pts)") {
    val combiner1 = new DLLCombinerTest
    combiner1 += 7
    combiner1 += 2

    val combiner2 = new DLLCombinerTest
    combiner2 += 3
    combiner2 += 8

    val combiner3 = new DLLCombinerTest
    combiner3 += 1
    combiner3 += 9

    val combiner4 = new DLLCombinerTest
    combiner4 += 3
    combiner4 += 2

    val result = combiner1.combine(combiner2).combine(combiner3).combine(combiner4).result()
    val array = Array(7, 2, 3, 8, 1, 9, 3, 2)

    assert(Range(0, array.size).forall(i => array(i) == result(i)))
  }

  test("[Public] fetch result - small combiner (2pts)") {
    val combiner1 = new DLLCombinerTest
    combiner1 += 4
    combiner1 += 2
    combiner1 += 6

    val result = combiner1.result()
    val array = Array(4, 2, 6)

    assert(Range(0, array.size).forall(i => array(i) == result(i)))
  }


  // (25+) 15 / 250 points for correct implementation, don't check parallelism
  test("[Correctness] fetch result - simple combiners (2pts)") {
    assertCorrectnessSimple()
  }

  test("[Correctness] fetch result - small combiners (3pts)") {
    assertCorrectnessBasic()
  }

  test("[Correctness] fetch result - small combiners after combining (5pts)") {
    assertCorrectnessCombined()
  }

  test("[Correctness] fetch result - large combiners (5pts)") {
    assertCorrectnessLarge()
  }

  def assertCorrectnessSimple() = simpleCombiners.foreach(elem => assert(compare(elem._1, elem._2)))

  def assertCorrectnessBasic() = basicCombiners.foreach(elem => assert(compare(elem._1, elem._2)))

  def assertCorrectnessCombined() =
    combinedCombiners.foreach(elem => assert(compare(elem._1, elem._2)))

  def assertCorrectnessLarge() = largeCombiners.foreach(elem => assert(compare(elem._1, elem._2)))

  // (25+15+) 25 / 250 points for correct parallel implementation, don't check if it's exactly 1/4 of the array per task
  private var count = 0
  private val expected = 3

  override def task[T](body: => T): ForkJoinTask[T] =
    count += 1
    scheduler.value.schedule(body)

  test("[TaskCount] number of newly created tasks should be 3 (5pts)") {
    assertTaskCountSimple()
  }

  test("[TaskCount] fetch result and check parallel - simple combiners (5pts)") {
    assertTaskCountSimple()
    assertCorrectnessSimple()
  }

  test("[TaskCount] fetch result and check parallel - small combiners (5pts)") {
    assertTaskCountSimple()
    assertCorrectnessBasic()
  }

  test("[TaskCount] fetch result and check parallel - small combiners after combining (5pts)") {
    assertTaskCountSimple()
    assertCorrectnessCombined()
  }

  test("[TaskCount] fetch result and check parallel - large combiners (5pts)") {
    assertTaskCountSimple()
    assertCorrectnessLarge()
  }

  def assertTaskCountSimple(): Unit =
    simpleCombiners.foreach(elem => assertTaskCount(elem._1, elem._2))

  def assertTaskCount(combiner: DLLCombinerTest, array: Array[Int]): Unit =
    try
      count = 0
      build(combiner, array)
      combiner.result()
      assertEquals(
        count,
        expected, {
          s"ERROR: Expected $expected instead of $count calls to `task(...)`"
        }
      )
    finally count = 0

  // (25+15+25+) 50 / 250 points for correct implementation that uses only next2 and previous2, and not next and previous
  test("[Skip2] fetch parallel result and check skip2 - simple combiners (10pts)") {
    assertTaskCountSimple()
    assertSkipSimple()
    assertCorrectnessSimple()
  }

  test("[Skip2] fetch result and check skip2 - simple combiners (10pts)") {
    assertSkipSimple()
    assertCorrectnessSimple()
  }

  test("[Skip2] fetch result and check skip2 - small combiners (10pts)") {
    assertSkipSimple()
    assertCorrectnessBasic()
  }

  test("[Skip2] fetch result and check skip2 - small combiners after combining (10pts)") {
    assertSkipSimple()
    assertCorrectnessCombined()
  }

  test("[Skip2] fetch result and check skip2 - large combiners (10pts)") {
    assertSkipSimple()
    assertCorrectnessLarge()
  }

  def assertSkipSimple(): Unit = simpleCombiners.foreach(elem => assertSkip(elem._1, elem._2))

  def assertSkip(combiner: DLLCombinerTest, array: Array[Int]): Unit =
    build(combiner, array)
    combiner.result()
    assertEquals(
      combiner.nonSkipped,
      false, {
        s"ERROR: Calls to 'next' and 'previous' are not allowed! You should only use 'next2` and 'previous2' in your solution."
      }
    )

  // (25+15+25+50+) 75 / 250 points for correct parallel implementation, exactly 1/4 of the array per task
  test("[TaskFairness] each task should compute 1/4 of the result (15pts)") {
    assertTaskFairness(simpleCombiners.unzip._1)
  }

  test(
    "[TaskFairness] each task should correctly compute 1/4 of the result - simple combiners (15pts)"
  ) {
    assertTaskFairness(simpleCombiners.unzip._1)
    assertCorrectnessSimple()
  }

  test(
    "[TaskFairness] each task should correctly compute 1/4 of the result - small combiners (15pts)"
  ) {
    assertTaskFairness(basicCombiners.unzip._1)
    assertCorrectnessBasic()
  }

  test(
    "[TaskFairness] each task should correctly compute 1/4 of the result - small combiners after combining (15pts)"
  ) {
    assertTaskFairness(combinedCombiners.unzip._1)
    assertCorrectnessCombined()
  }

  test(
    "[TaskFairness] each task should correctly compute 1/4 of the result - large combiners (15pts)"
  ) {
    assertTaskFairness(largeCombiners.unzip._1)
    assertCorrectnessLarge()
  }

  def assertTaskFairness(combiners: List[DLLCombiner]): Unit =
    def assertNewTaskFairness(combiner: DLLCombiner, task: ForkJoinTask[Unit], data: Array[Int]) =
      var count = 0
      var expected = combiner.size / 4
      task.join
      count = data.count(elem => elem != 0)
      assert((count - expected).abs <= 1)

    def assertMainTaskFairness(combiner: DLLCombiner, task: Unit, data: Array[Int]) =
      var count = 0
      var expected = combiner.size / 4
      count = data.count(elem => elem != 0)
      assert((count - expected).abs <= 1)

    combiners.foreach { elem =>
      var data = Array.fill(elem.size)(0)
      assertNewTaskFairness(elem, elem.task1(data), data)

      data = Array.fill(elem.size)(0)
      assertNewTaskFairness(elem, elem.task2(data), data)

      data = Array.fill(elem.size)(0)
      assertNewTaskFairness(elem, elem.task3(data), data)

      data = Array.fill(elem.size)(0)
      assertMainTaskFairness(elem, elem.task4(data), data)
    }

  // (25+15+25+50+75+) 60 / 250 points for correct parallel implementation, exactly 1/4 of the array per task, exactly the specified quarter

  test(
    "[TaskPrecision] each task should compute specified 1/4 of the result - simple combiners (10pts)"
  ) {
    assertTaskPrecision(simpleCombiners)
  }

  test(
    "[TaskPrecision] task1 should compute specified 1/4 of the result - simple combiners (5pts)"
  ) {
    assertTaskPrecision1(simpleCombiners)
  }

  test(
    "[TaskPrecision] task2 should compute specified 1/4 of the result - simple combiners (5pts)"
  ) {
    assertTaskPrecision2(simpleCombiners)
  }

  test(
    "[TaskPrecision] task3 should compute specified 1/4 of the result - simple combiners (5pts)"
  ) {
    assertTaskPrecision3(simpleCombiners)
  }

  test(
    "[TaskPrecision] task4 should compute specified 1/4 of the result - simple combiners (5pts)"
  ) {
    assertTaskPrecision4(simpleCombiners)
  }

  test(
    "[TaskPrecision] each task should compute specified 1/4 of the result - other combiners (30pts)"
  ) {
    assertTaskPrecision(basicCombiners)
    assertTaskPrecision(combinedCombiners)
    assertTaskPrecision(largeCombiners)
  }

  def assertTaskPrecision(combiners: List[(DLLCombiner, Array[Int])]): Unit =
    assertTaskPrecision1(combiners)
    assertTaskPrecision2(combiners)
    assertTaskPrecision3(combiners)
    assertTaskPrecision4(combiners)

  def assertTaskPrecision1(combiners: List[(DLLCombiner, Array[Int])]): Unit =
    combiners.foreach { elem =>
      var data = Array.fill(elem._1.size)(0)
      var ref = Array.fill(elem._1.size)(0)
      val task1 = elem._1.task1(data)
      task1.join
      Range(0, elem._1.size).foreach(i =>
        (if i < elem._1.size / 2 - 1 && i % 2 == 0 then ref(i) = elem._2(i))
      )
      assert(Range(0, elem._1.size / 2 - 1).forall(i => data(i) == ref(i)))
    }

  def assertTaskPrecision2(combiners: List[(DLLCombiner, Array[Int])]): Unit =
    combiners.foreach { elem =>  
      var data = Array.fill(elem._1.size)(0)
      var ref = Array.fill(elem._1.size)(0)
      val task2 = elem._1.task2(data)
      task2.join
      Range(0, elem._1.size).foreach(i =>
        (if i < elem._1.size / 2 - 1 && i % 2 == 1 then ref(i) = elem._2(i))
      )
      assert(Range(0, elem._1.size / 2 - 1).forall(i => data(i) == ref(i)))
  }

  def assertTaskPrecision3(combiners: List[(DLLCombiner, Array[Int])]): Unit =
    combiners.foreach { elem => 
      var data = Array.fill(elem._1.size)(0)
      var ref = Array.fill(elem._1.size)(0)
      val task3 = elem._1.task3(data)
      task3.join
      Range(0, elem._1.size).foreach(i =>
        (if i > elem._1.size / 2 + 1 && i % 2 == elem._1.size % 2 then ref(i) = elem._2(i))
      )
      assert(Range(elem._1.size / 2 + 2, elem._1.size).forall(i => data(i) == ref(i)))
    }

  def assertTaskPrecision4(combiners: List[(DLLCombiner, Array[Int])]): Unit =
    combiners.foreach { elem =>  
      var data = Array.fill(elem._1.size)(0)
      var ref = Array.fill(elem._1.size)(0)
      val task4 = elem._1.task4(data)
      Range(0, elem._1.size).foreach(i =>
        (if i > elem._1.size / 2 + 1 && i % 2 != elem._1.size % 2 then ref(i) = elem._2(i))
      )
      assert(Range(elem._1.size / 2 + 2, elem._1.size).forall(i => data(i) == ref(i)))
    }

trait AbstractProblem1Suite extends munit.FunSuite with LibImpl:

  def simpleCombiners = buildSimpleCombiners()
  def basicCombiners = buildBasicCombiners()
  def combinedCombiners = buildCombinedCombiners()
  def largeCombiners = buildLargeCombiners()

  def buildSimpleCombiners() =
    val simpleCombiners = List(
      (new DLLCombinerTest, Array(4, 2, 6, 1, 5, 4, 3, 5, 6, 3, 4, 5, 6, 3, 4, 5)),
      (new DLLCombinerTest, Array(7, 2, 2, 9, 3, 2, 1, 1, 1, 1, 1, 1, 1, 2, 3, 2)),
      (new DLLCombinerTest, Array.fill(16)(5))
    )
    simpleCombiners.foreach(elem => build(elem._1, elem._2))
    simpleCombiners

  def buildBasicCombiners() =
    val basicCombiners = List(
      (new DLLCombinerTest, Array(4, 2, 6)),
      (new DLLCombinerTest, Array(4, 1, 6)),
      (new DLLCombinerTest, Array(7, 2, 2, 9, 3, 2, 11, 12, 5, 14, 15, 1, 17, 23)),
      (new DLLCombinerTest, Array(7, 2, 9, 9, 3, 2, 11, 12, 13, 14, 15, 16, 17, 22)),
      (new DLLCombinerTest, Array.fill(16)(7)),
      (new DLLCombinerTest, Array.fill(16)(4)),
      (new DLLCombinerTest, Array.fill(5)(3)),
      (new DLLCombinerTest, Array.fill(5)(7)),
      (new DLLCombinerTest, Array.fill(5)(4))
    )
    basicCombiners.foreach(elem => build(elem._1, elem._2))
    basicCombiners

  def buildCombinedCombiners() =
    var combinedCombiners = List[(DLLCombiner, Array[Int])]()

    Range(1, 10).foreach { n =>
      val array = basicCombiners.filter(elem => elem._1.size == n).foldLeft(Array[Int]()) {
        (acc, i) => acc ++ i._2
      }
      val empty: DLLCombiner = new DLLCombinerTest
      val combiner = basicCombiners.filter(elem => elem._1.size == n).map(_._1).foldLeft(empty) {
        (acc, c) => acc.combine(c)
      }

      combinedCombiners = combinedCombiners :+ (combiner, array)
    }
    combinedCombiners

  def buildLargeCombiners() =
    val largeCombiners = List(
      (new DLLCombinerTest, Array.fill(1321)(4) ++ Array.fill(1322)(7)),
      (new DLLCombinerTest, Array.fill(1341)(2) ++ Array.fill(1122)(5)),
      (
        new DLLCombinerTest,
        Array.fill(1321)(4) ++ Array.fill(1322)(7) ++ Array.fill(321)(4) ++ Array.fill(322)(7)
      ),
      (new DLLCombinerTest, Array.fill(992321)(4) ++ Array.fill(99322)(7)),
      (new DLLCombinerTest, Array.fill(953211)(4) ++ Array.fill(999322)(1))
    )
    largeCombiners.foreach(elem => build(elem._1, elem._2))
    largeCombiners

  def build(combiner: DLLCombinerTest, array: Array[Int]): DLLCombinerTest =
    array.foreach(elem => combiner += elem)
    combiner

  def compare(combiner: DLLCombiner, array: Array[Int]): Boolean =
    val result = combiner.result()
    Range(0, array.size).forall(i => array(i) == result(i))

  def buildAndCompare(combiner: DLLCombinerTest, array: Array[Int]): Boolean =
    array.foreach(elem => combiner += elem)
    val result = combiner.result()
    Range(0, array.size).forall(i => array(i) == result(i))

trait LibImpl extends Problem1:

  val forkJoinPool = new ForkJoinPool

  abstract class TaskScheduler:
    def schedule[T](body: => T): ForkJoinTask[T]

  class DefaultTaskScheduler extends TaskScheduler:
    def schedule[T](body: => T): ForkJoinTask[T] =
      val t = new RecursiveTask[T]:
        def compute = body
      Thread.currentThread match
        case wt: ForkJoinWorkerThread =>
          t.fork()
        case _ =>
          forkJoinPool.execute(t)
      t

  val scheduler = new DynamicVariable[TaskScheduler](new DefaultTaskScheduler)

  def task[T](body: => T): ForkJoinTask[T] = scheduler.value.schedule(body)

  class NodeTest(val v: Int, val myCombiner: DLLCombinerTest) extends Node(v):
    override def getNext: Node =
      myCombiner.nonSkipped = true
      next
    override def getNext2: Node = next2
    override def getPrevious: Node =
      myCombiner.nonSkipped = true
      previous
    override def getPrevious2: Node = previous2
    override def setNext(n: Node): Unit = next = n
    override def setNext2(n: Node): Unit = next2 = n
    override def setPrevious(n: Node): Unit = previous = n
    override def setPrevious2(n: Node): Unit = previous2 = n

  class DLLCombinerTest extends DLLCombinerImplementation:
    var nonSkipped = false
    override def result(): Array[Int] =
      nonSkipped = false
      super.result()
    override def +=(elem: Int): Unit =
      val node = new NodeTest(elem, this)
      if size == 0 then
        first = node
        last = node
        size = 1
      else
        last.setNext(node)
        node.setPrevious(last)
        node.setPrevious2(last.getPrevious)
        if size > 1 then last.getPrevious.setNext2(node)
        else second = node
        secondToLast = last
        last = node
        size += 1
    override def combine(that: DLLCombiner): DLLCombiner =
      if this.size == 0 then that
      else if that.size == 0 then this
      else
        this.last.setNext(that.first)
        this.last.setNext2(that.first.getNext)
        if this.last.getPrevious != null then
          this.last.getPrevious.setNext2(that.first) // important

        that.first.setPrevious(this.last)
        that.first.setPrevious2(this.last.getPrevious)
        if that.first.getNext != null then that.first.getNext.setPrevious2(this.last) // important

        if this.size == 1 then second = that.first

        this.size = this.size + that.size
        this.last = that.last
        this.secondToLast = that.secondToLast

        this
