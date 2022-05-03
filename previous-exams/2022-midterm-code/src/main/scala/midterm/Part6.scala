package midterm

import midterm.instrumentation.Monitor

// Question 21

// See tests in midterm.Part6Test.
// Run with `sbt "testOnly midterm.Part6Test"`.

class TicketsManager(totalTickets: Int) extends Monitor:
  var remainingTickets = totalTickets

  // This method might be called concurrently
  def getTicket(): Boolean =
    if remainingTickets > 0 then
      this.synchronized {
        remainingTickets -= 1
      }
      true
    else false
