import scala.util.{Try, Success, Failure}
import org.junit.Test
import org.junit.Assert.{assertEquals, fail}

class Problem1Test:
  object MyFuture:
    final def successful[T](value: T): MyFuture[T] =
      new MyFuture:
        def onComplete(callback: Try[T] => Unit): Unit =
          callback(Success(value))

    final def failed[T](error: Error): MyFuture[T] =
      new MyFuture:
        def onComplete(callback: Try[T] => Unit): Unit =
          callback(Failure(error))

  @Test
  def mapWorksWithSuccess() =
    MyFuture.successful(3).map(_ + 1).onComplete {
      case Success(value) => assertEquals(4, value)
      case _              => fail("Expected result to be a Success")
    }

  @Test
  def mapWorksWithFailure() =
    val error = new Error("Some error")
    MyFuture.failed[Int](error).map(_ + 1).onComplete {
      case Failure(actualError) => assertEquals(error, actualError)
      case _                    => fail("Expected result to be a Failure")
    }

  @Test
  def filterWorksWithSuccessNotFilteredOut() =
    MyFuture.successful(3).filter(_ == 3).onComplete {
      case Success(value) => assertEquals(3, value)
      case _              => fail("Expected result to be a Failure")
    }

  @Test
  def filterWorksWithSuccessFilteredOut() =
    MyFuture.successful(3).filter(_ == 4).onComplete {
      case Failure(actualError: NoSuchElementException) => ()
      case _ => fail("Expected result to be a NoSuchElementException exception")
    }

  @Test
  def filterWorksWithFailure() =
    val error = new Error("Some error")
    MyFuture.failed[Int](error).filter(_ == 3).onComplete {
      case Failure(actualError) => assertEquals(error, actualError)
      case _                    => fail("Expected result to be a Failure")
    }
