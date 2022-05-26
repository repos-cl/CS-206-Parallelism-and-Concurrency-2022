package concpar21final03.instrumentation

class AtomicReference[T](initial: T):

  private val atomic =
    new java.util.concurrent.atomic.AtomicReference[T](initial)

  def get: T = atomic.get()

  def set(value: T): Unit = atomic.set(value)
