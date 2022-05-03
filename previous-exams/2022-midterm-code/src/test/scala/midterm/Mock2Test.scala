package midterm

import org.junit.*
import org.junit.Assert.*
import instrumentation.*

class Mock2Test:
  @Test
  def test() =
    TestUtils.assertDeadlock(
      TestHelper.testManySchedules(
        2,
        scheduler =>
          val a = new ScheduledAccount(50, scheduler)
          val b = new ScheduledAccount(70, scheduler)

          (
            List(
              () => a.transfer(b, 10),
              () => b.transfer(a, 10)
            ),
            results => (true, "")
          )
      )
    )

  class ScheduledAccount(n: Int, val scheduler: Scheduler)
      extends Account(n)
      with MockedMonitor
