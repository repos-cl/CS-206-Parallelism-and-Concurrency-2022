package m20.instrumentation

import java.util.concurrent.atomic._

abstract class AbstractAtomicVariable[T] {
  def get: T
  def set(value: T): Unit
  def compareAndSet(expect: T, newval: T) : Boolean
}

class AtomicVariable[T](initial: T) extends AbstractAtomicVariable[T] {

  private val atomic = new AtomicReference[T](initial)

  override def get: T = atomic.get()

  override def set(value: T): Unit = atomic.set(value)

  override def compareAndSet(expected: T, newValue: T): Boolean = {
    val current = atomic.get
    if (current == expected) {
      atomic.compareAndSet(current, newValue)
    }
    else {
      false
    }
  }
}
