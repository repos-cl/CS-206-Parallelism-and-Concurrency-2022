package midterm.instrumentation

class Dummy

trait Monitor:
  given dummy: Dummy = new Dummy

  def wait()(implicit i: Dummy = dummy) = waitDefault()

  def synchronized[T](e: => T)(implicit i: Dummy = dummy) = synchronizedDefault(
    e
  )

  def notify()(implicit i: Dummy = dummy) = notifyDefault()

  def notifyAll()(implicit i: Dummy = dummy) = notifyAllDefault()

  private val lock = new AnyRef

  // Can be overridden.
  def waitDefault(): Unit = lock.wait()
  def synchronizedDefault[T](toExecute: => T): T = lock.synchronized(toExecute)
  def notifyDefault(): Unit = lock.notify()
  def notifyAllDefault(): Unit = lock.notifyAll()

trait LockFreeMonitor extends Monitor:
  override def waitDefault() =
    throw new Exception("Please use lock-free structures and do not use wait()")
  override def synchronizedDefault[T](toExecute: => T): T =
    throw new Exception(
      "Please use lock-free structures and do not use synchronized()"
    )
  override def notifyDefault() =
    throw new Exception(
      "Please use lock-free structures and do not use notify()"
    )
  override def notifyAllDefault() =
    throw new Exception(
      "Please use lock-free structures and do not use notifyAll()"
    )
