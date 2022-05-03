package midterm

import midterm.instrumentation.Monitor
import midterm.instrumentation.MockedMonitor

import org.junit.*
import org.junit.Assert.*
import instrumentation.*
import java.util.concurrent.atomic.AtomicInteger

class Part4Test:
 
  // This test can result in a deadlock because locks can be called in any
  // order. Here, Thread 1 locks Node 3 first and then Node 2, whereas Thread 2
  // locks Node 2 first and then Node 3. This will lead to a deadlock. 
  @Test
  def testQuestion9() =
    TestUtils.assertDeadlock(
      TestHelper.testManySchedules(
        2,
        scheduler =>
          val allNodes = (for i <- 0 to 6 yield ScheduledNode(i, scheduler)).toList

          // Shared by all threads
          var sum: Int = 0
          def increment(e: Int) = sum += e

          (
            List(
              () =>
                // Thread 1
                var nodes: List[Node] = List(allNodes(1), allNodes(3), allNodes(2), allNodes(4))
                nodes = nodes
                lockFun(nodes, increment),
              () =>
                // Thread 2
                var nodes: List[Node] = List(allNodes(5), allNodes(2), allNodes(3))
                nodes = nodes
                lockFun(nodes, increment),
            ),
            results => (true, "")
          )
      )
    )
  
  // This will not lead to a deadlock because the lock acquire happens in a
  // particular order. Thread 1 acquires locks in order 1->2->3->4, whereas
  // Thread 2 acquires locks in order 2->3->5.
  @Test
  def testQuestion10() =
    TestHelper.testManySchedules(
      2,
      scheduler =>
        val allNodes = (for i <- 0 to 6 yield ScheduledNode(i, scheduler)).toList

        // Shared by all threads
        var sum: Int = 0
        def increment(e: Int) = sum += e

        (
          List(
            () =>
              // Thread 1
              var nodes: List[Node] = List(allNodes(1), allNodes(3), allNodes(2), allNodes(4))
              nodes = nodes.sortWith((x, y) => x.guid > y.guid)
              lockFun(nodes, increment),
            () =>
              // Thread 2
              var nodes: List[Node] = List(allNodes(5), allNodes(2), allNodes(3))
              nodes = nodes.sortWith((x, y) => x.guid > y.guid)
              lockFun(nodes, increment),
          ),
          results => (true, "")
        )
    )


  // This will not lead to a deadlock because the lock acquire happens in a
  // particular order. Thread 1 acquires locks in order 4->3->2->1, whereas
  // Thread 2 acquires locks in order 5->3->2. 
  @Test
  def testQuestion11() =
    TestHelper.testManySchedules(
      2,
      scheduler =>
        val allNodes = (for i <- 0 to 6 yield ScheduledNode(i, scheduler)).toList

        // Shared by all threads
        var sum: Int = 0
        def increment(e: Int) = sum += e

        (
          List(
            () =>
              // Thread 1
              var nodes: List[Node] = List(allNodes(1), allNodes(3), allNodes(2), allNodes(4))
              nodes = nodes.sortWith((x, y) => x.guid < y.guid)
              lockFun(nodes, increment),
            () =>
              // Thread 2
              var nodes: List[Node] = List(allNodes(5), allNodes(2), allNodes(3))
              nodes = nodes.sortWith((x, y) => x.guid < y.guid)
              lockFun(nodes, increment),
          ),
          results => (true, "")
        )
    )

  // This test can result in a deadlock because locks are not called in any
  // order. Thread 1 acquire order (3->2->4->1), Thread 2 acquire order
  // (2->3->5). Thread 1 locks Node3 first and then Node2, whereas Thread 2
  // locks Node 2 first and then Node3. This will lead to a deadlock. 
  @Test
  def testQuestion12() =
    TestUtils.assertDeadlock(
      TestHelper.testManySchedules(
        2,
        scheduler =>
          val allNodes = (for i <- 0 to 6 yield ScheduledNode(i, scheduler)).toList

          // Shared by all threads
          var sum: Int = 0
          def increment(e: Int) = sum += e

          (
            List(
              () =>
                // Thread 1
                var nodes: List[Node] = List(allNodes(1), allNodes(3), allNodes(2), allNodes(4))
                nodes = nodes.tail.appended(nodes(0))
                lockFun(nodes, increment),
              () =>
                // Thread 2
                var nodes: List[Node] = List(allNodes(5), allNodes(2), allNodes(3))
                nodes = nodes.tail.appended(nodes(0))
                lockFun(nodes, increment),
            ),
            results => (true, "")
          )
      )
    )
 
  // sum returns wrong answer because there is a data race on the sum variable. 
  @Test(expected = classOf[AssertionError])
  def testQuestion13() =
    TestHelper.testManySchedules(
      2,
      scheduler =>
        val allNodes = (for i <- 0 to 6 yield ScheduledNode(i, scheduler)).toList

        // Shared by all threads
        var sum: Int = 0
        def increment(e: Int) =
          val previousSum = scheduler.exec{sum}("Get sum")
          scheduler.exec{sum = previousSum + e}("Write sum")

        (
          List(
            () =>
              // Thread 1
              var nodes: List[Node] = List(allNodes(1), allNodes(3), allNodes(2), allNodes(4))
              nodes = nodes.sortWith((x, y) => x.guid < y.guid)
              lockFun(nodes, increment),
            () =>
              // Thread 2
              var nodes: List[Node] = List(allNodes(5), allNodes(2), allNodes(3))
              nodes = nodes.sortWith((x, y) => x.guid < y.guid)
              lockFun(nodes, increment),
          ),
          results =>
            if sum != 20 then
              (false, f"Wrong sum: expected 20 but got $sum")
            else
              (true, "")
        )
    )

  // sum value will be correct here because "sum += e" is protected by a lock.
  @Test
  def testQuestion14() =
    TestHelper.testManySchedules(
      2,
      sched =>
        val allNodes = (for i <- 0 to 6 yield ScheduledNode(i, sched)).toList

        val monitor = new MockedMonitor: // Monitor is a type of a lock.
         def scheduler = sched

        // Shared by all threads
        var sum: Int = 0
        def increment(e: Int) =
          monitor.synchronized { sum += e }

        (
          List(
            () =>
              // Thread 1
              var nodes: List[Node] = List(allNodes(1), allNodes(3), allNodes(2), allNodes(4))
              nodes = nodes.sortWith((x, y) => x.guid < y.guid)
              lockFun(nodes, increment),
            () =>
              // Thread 2
              var nodes: List[Node] = List(allNodes(5), allNodes(2), allNodes(3))
              nodes = nodes.sortWith((x, y) => x.guid < y.guid)
              lockFun(nodes, increment),
          ),
          results =>
            if sum != 20 then
              (false, f"Wrong sum: expected 20 but got $sum")
            else
              (true, "")
        )
    )
  
  // total will give correct output here as it is an atomic instruction.
  @Test
  def testQuestion15() =
    TestHelper.testManySchedules(
      2,
      sched =>
        val allNodes = (for i <- 0 to 6 yield ScheduledNode(i, sched)).toList

        // Shared by all threads
        var total: AtomicInteger = new AtomicInteger(0)
        def increment(e: Int) =
          total.addAndGet(e)

        (
          List(
            () =>
              // Thread 1
              var nodes: List[Node] = List(allNodes(1), allNodes(3), allNodes(2), allNodes(4))
              nodes = nodes.sortWith((x, y) => x.guid < y.guid)
              lockFun(nodes, increment),
            () =>
              // Thread 2
              var nodes: List[Node] = List(allNodes(5), allNodes(2), allNodes(3))
              nodes = nodes.sortWith((x, y) => x.guid < y.guid)
              lockFun(nodes, increment),
          ),
          results =>
            if total.get != 20 then
              (false, f"Wrong total: expected 20 but got $total")
            else
              (true, "")
        )
    )
  
  
  class ScheduledNode(value: Int, val scheduler: Scheduler) extends Node(value) with MockedMonitor
