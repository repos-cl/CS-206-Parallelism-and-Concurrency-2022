package midterm

import org.junit.*
import org.junit.Assert.*
import midterm.instrumentation.*

class Part6Test:
  @Test(expected = classOf[AssertionError])
  def testQuestion21() =
    TestHelper.testManySchedules(
      2,
      scheduler =>
        val ticketsManager = ScheduledTicketsManager(1, scheduler)

        (
          List(
            () =>
              // Thread 1
              ticketsManager.getTicket(),
            () =>
              // Thread 2
              ticketsManager.getTicket()
          ),
          results =>
            if ticketsManager.remainingTickets < 0 then
              (false, "Sold more tickets than available!")
            else (true, "")
        )
    )

  class ScheduledTicketsManager(totalTickets: Int, val scheduler: Scheduler)
      extends TicketsManager(totalTickets)
      with MockedMonitor
