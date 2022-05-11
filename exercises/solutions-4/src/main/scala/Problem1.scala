import scala.util.{Try, Success, Failure}
import scala.concurrent.ExecutionContext
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicReference

// Test using `sbt "testOnly Problem1Test"`
// See tests in Problem1Test.scala

trait MyFuture[+T]:
  def onComplete(callback: Try[T] => Unit): Unit

extension [T](self: MyFuture[T])
  def map[S](f: T => S): MyFuture[S] =
    new MyFuture:
      def onComplete(callback: Try[S] => Unit): Unit =
        self.onComplete {
          case Success(v) => callback(Success(f(v)))
          case Failure(e) => callback(Failure(e))
        }
  def filter(p: T => Boolean): MyFuture[T] =
    new MyFuture:
      def onComplete(callback: Try[T] => Unit): Unit =
        self.onComplete {
          case Success(v) =>
            if p(v) then callback(Success(v))
            else callback(Failure(new NoSuchElementException()))
          case Failure(e) => callback(Failure(e))
        }
