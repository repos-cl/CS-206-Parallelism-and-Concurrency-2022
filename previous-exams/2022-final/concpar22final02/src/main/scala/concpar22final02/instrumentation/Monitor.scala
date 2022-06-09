package concpar22final02.instrumentation

class Dummy

trait Monitor:
  implicit val dummy: Dummy = new Dummy

  def wait()(implicit i: Dummy) = waitDefault()

  def synchronized[T](e: => T)(implicit i: Dummy) = synchronizedDefault(e)

  def notify()(implicit i: Dummy) = notifyDefault()

  def notifyAll()(implicit i: Dummy) = notifyAllDefault()

  private val lock = new AnyRef

  // Can be overriden.
  def waitDefault(): Unit = lock.wait()
  def synchronizedDefault[T](toExecute: => T): T = lock.synchronized(toExecute)
  def notifyDefault(): Unit = lock.notify()
  def notifyAllDefault(): Unit = lock.notifyAll()

trait LockFreeMonitor extends Monitor:
  override def waitDefault() =
    throw new Exception("Please use lock-free structures and do not use wait()")
  override def synchronizedDefault[T](toExecute: => T): T =
    throw new Exception("Please use lock-free structures and do not use synchronized()")
  override def notifyDefault() =
    throw new Exception("Please use lock-free structures and do not use notify()")
  override def notifyAllDefault() =
    throw new Exception("Please use lock-free structures and do not use notifyAll()")
