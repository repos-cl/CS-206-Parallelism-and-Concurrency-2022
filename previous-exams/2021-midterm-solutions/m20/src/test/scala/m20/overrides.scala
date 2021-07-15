package m20

import instrumentation._

import scala.annotation.tailrec
import java.util.concurrent.atomic._

class SchedulableAtomicVariable[T](initial: T, scheduler: Scheduler, name: String) extends AbstractAtomicVariable[T]:
  private val proxied: AtomicVariable[T] = new AtomicVariable[T](initial)

  override def get: T = scheduler.exec {
    proxied.get
  } (s"", Some(res => s"$name: get $res"))

  override def set(value: T): Unit = scheduler.exec {
    proxied.set(value)
  } (s"$name: set $value", None)

  override def compareAndSet(expected: T, newValue: T): Boolean = {
    scheduler.exec {
      proxied.compareAndSet(expected, newValue)
    } (s"$name: compareAndSet(expected = $expected, newValue = $newValue)", Some(res => s"$name: Did it set? $res") )
  }

end SchedulableAtomicVariable

class SchedulableSeqCount(val scheduler: Scheduler) extends SeqCount with LockFreeMonitor:
  override def generation: Int = scheduler.exec {
    super.generation
  } ("", Some(res => s"generation is $res"))
  override def setGeneration(newGeneration: Int): Unit = scheduler.exec {
    super.setGeneration(newGeneration)
  } ( s"setGeneration($newGeneration)", None )

  override def x: Int = scheduler.exec {
    super.x
  } ("", Some(res => s"x is $res"))
  override def setX(newX: Int): Unit = scheduler.exec {
    super.setX(newX)
  } (s"setX($newX)", None)

  override def y: Int = scheduler.exec {
    super.y
  } ("", Some(res => s"y is $res"))
  override def setY(newY: Int): Unit = scheduler.exec {
    super.setY(newY)
  } (s"setY($newY)", None)

end SchedulableSeqCount

class SchedulableMultiWriterSeqCount(val scheduler: Scheduler) extends MultiWriterSeqCount with LockFreeMonitor:
  override protected val myGeneration: AbstractAtomicVariable[Int] = new SchedulableAtomicVariable(0, scheduler, "myGeneration")

  override def x: Int = scheduler.exec {
    super.x
  } ("", Some(res => s"x is $res"))
  override def setX(newX: Int): Unit = scheduler.exec {
    super.setX(newX)
  } (s"setX($newX)", None)

  override def y: Int = scheduler.exec {
    super.y
  } ("", Some(res => s"y is $res"))
  override def setY(newY: Int): Unit = scheduler.exec {
    super.setY(newY)
  } (s"setY($newY)", None)

end SchedulableMultiWriterSeqCount
