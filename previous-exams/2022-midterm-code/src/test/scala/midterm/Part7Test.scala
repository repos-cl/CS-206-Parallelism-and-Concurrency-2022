package midterm

import org.junit.*
import org.junit.Assert.*
import midterm.instrumentation.*

class Part7Test:
  @Test
  def testNicManagerSequential() =
    val nicsManager = NICManager(4)
    assertEquals((0, 1), nicsManager.assignNICs())
    assertEquals((2, 3), nicsManager.assignNICs())

  @Test
  def testQuestion22() =
    testNicManagerParallel(2, 3)
  
  @Test
  def testQuestion23() =
    val nicsManager = NICManager(2)

    // Thread 1
    assertEquals((0, 1), nicsManager.assignNICs())
    nicsManager.nics(0).assigned = false
    nicsManager.nics(1).assigned = false

    // Thread 2
    assertEquals((0, 1), nicsManager.assignNICs())
    nicsManager.nics(0).assigned = false
    nicsManager.nics(1).assigned = false

  @Test
  def testQuestion24() =
    testNicManagerParallel(3, 2, true)
  
  @Test
  def testQuestion24NotLimitingRecvNICs() =
    TestUtils.assertMaybeDeadlock(
     testNicManagerParallel(3, 2)
    )

  def testNicManagerParallel(
      threads: Int,
      nics: Int,
      limitRecvNICs: Boolean = false
  ) =
    TestHelper.testManySchedules(
      threads,
      scheduler =>
        val nicsManager = ScheduledNicsManager(nics, scheduler)
        val tasks = for i <- 0 until threads yield () =>
          // Thread i
          val (recvNIC, sendNIC) = nicsManager.assignNICs(limitRecvNICs)

          // Do something with NICs...

          // Un-assign NICs
          nicsManager.nics(recvNIC).assigned = false
          nicsManager.nics(sendNIC).assigned = false
        (
          tasks.toList,
          results =>
            if nicsManager.nics.count(_.assigned) != 0 then
              (false, f"All NICs should have been released.")
            else (true, "")
        )
    )

  class ScheduledNicsManager(n: Int, scheduler: Scheduler)
      extends NICManager(n):
    class ScheduledNIC(
        _index: Int,
        _assigned: Boolean,
        val scheduler: Scheduler
    ) extends NIC(_index, _assigned)
        with MockedMonitor:
      override def index = scheduler.exec { super.index }(
        "",
        Some(res => f"read NIC.index == $res")
      )
      override def assigned = scheduler.exec { super.assigned }(
        "",
        Some(res => f"read NIC.assigned == $res")
      )
      override def assigned_=(v: Boolean) = scheduler.exec { super.assigned = v }(
        f"write NIC.assigned = $v"
      )
    override val nics =
      (for i <- 0 until n yield ScheduledNIC(i, false, scheduler)).toList
