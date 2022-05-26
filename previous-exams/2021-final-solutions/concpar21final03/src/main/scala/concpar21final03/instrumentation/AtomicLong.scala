package concpar21final03.instrumentation

/** A long value that may be updated atomically. */
class AtomicLong(initial: Long):

  private val atomic = new java.util.concurrent.atomic.AtomicLong(initial)

  /** Get the current value. */
  def get: Long = atomic.get()

  /** Set to the given `value`. */
  def set(value: Long): Unit = atomic.set(value)

  /** Atomically increment by one the current value and return the _original_
    * value.
    */
  def getAndIncrement(): Long =
    atomic.getAndIncrement()

  /** Atomically increment by one the current value and return the _updated_
    * value.
    */
  def incrementAndGet(): Long =
    atomic.incrementAndGet()

  /** Atomically set the value to `newValue` if the current value == `expected`.
    *
    * Return true if successful, otherwise return false to indicate that the
    * actual value was not equal to the expected value.
    */
  def compareAndSet(expected: Long, newValue: Long): Boolean =
    atomic.compareAndSet(expected, newValue)

end AtomicLong
