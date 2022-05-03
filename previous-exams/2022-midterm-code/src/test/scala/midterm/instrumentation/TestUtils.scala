package midterm.instrumentation

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import org.junit.Assert.*

object TestUtils:
  def failsOrTimesOut[T](action: => T): Boolean =
    val asyncAction = Future {
      action
    }
    try
      Await.result(asyncAction, 2000.millisecond)
    catch
      case _: Throwable => return true
    return false
  
  def assertDeadlock[T](action: => T): Unit =
    try
      action
      throw new AssertionError("No error detected.")
    catch
       case e: AssertionError =>
         assert(e.getMessage.contains("Deadlock"), "No deadlock detected.")
  
  def assertMaybeDeadlock[T](action: => T): Unit =
    try
      action
      throw new AssertionError("No error detected.")
    catch
       case e: AssertionError =>
         assert(e.getMessage.contains("A possible deadlock!"), "No deadlock detected.")
