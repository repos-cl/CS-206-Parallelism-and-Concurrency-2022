package m14

abstract class AbstractBlockingQueue[T] extends Monitor {
  private var underlying: List[T] = Nil

  def getUnderlying(): List[T] =
    underlying

  def setUnderlying(newValue: List[T]): Unit =
    underlying = newValue

  def put(elem: T): Unit
  def take(): T
}
