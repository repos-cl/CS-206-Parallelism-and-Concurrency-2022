package concpar21final03.instrumentation

import scala.concurrent.*
import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext.Implicits.global

object TestUtils:
  def failsOrTimesOut[T](action: => T): Boolean =
    val asyncAction = Future {
      action
    }
    try Await.result(asyncAction, 2000.millisecond)
    catch case _: Throwable => return true
    return false
