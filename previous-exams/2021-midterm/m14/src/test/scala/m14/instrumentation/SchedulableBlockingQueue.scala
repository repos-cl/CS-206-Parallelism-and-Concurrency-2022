package m14
package instrumentation

class SchedulableBlockingQueue[T](val scheduler: Scheduler)
  extends m14.M14.BlockingQueue[T] with MockedMonitor {
    private var underlying: List[T] = Nil

    override def getUnderlying(): List[T] =
      scheduler.exec {
        underlying
      }(s"Get $underlying")

    override def setUnderlying(newValue: List[T]): Unit =
      scheduler.exec {
        underlying = newValue
      }(s"Set $newValue")
}
