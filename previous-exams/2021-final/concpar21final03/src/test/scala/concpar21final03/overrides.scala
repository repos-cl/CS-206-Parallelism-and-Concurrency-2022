package concpar21final03

import instrumentation.*

class SchedulableThreadMap[A](val scheduler: Scheduler)
    extends ThreadMap[A]
    with MockedMonitor:

  override def currentThreadHasValue: Boolean = scheduler.exec {
    super.currentThreadHasValue
  }("", Some(res => s"currentThreadHasValue is $res"))

  override def currentThreadValue: Option[A] = scheduler.exec {
    super.currentThreadValue
  }("", Some(res => s"currentThreadValue is $res"))

  override def setCurrentThreadValue(value: A): Unit = scheduler.exec {
    super.setCurrentThreadValue(value)
  }(s"setCurrentThreadValue($value)")

  override def deleteCurrentThreadValue(): Unit = scheduler.exec {
    super.deleteCurrentThreadValue()
  }("deleteCurrentThreadValue()")

  override def waitForall(predicate: A => Boolean): Unit = scheduler.exec {
    super.waitForall(predicate)
  }("waitForall")

  def allValues: List[A] = synchronized {
    theMap.values.toList
  }

end SchedulableThreadMap

class SchedulableRCU(scheduler: Scheduler) extends RCU with LockFreeMonitor:
  override protected val latestVersion =
    SchedulableAtomicLong(0, scheduler, "latestVersion")
  override protected val readersVersion: ThreadMap[Long] = SchedulableThreadMap(
    scheduler
  )

class SchedulableInMemoryFileSystem(scheduler: Scheduler)
    extends InMemoryFileSystem:
  override def createFile(file: FileName, content: String): Unit =
    scheduler.exec {
      super.createFile(file, content)
    }(s"createFile($file)")
  override def readFile(file: FileName): String = scheduler.exec {
    super.readFile(file)
  }(s"readFile($file)")
  override def deleteFile(file: FileName): Unit = scheduler.exec {
    super.deleteFile(file)
  }(s"deleteFile($file)")

class SchedulableUpdateServer(scheduler: Scheduler, fs: InMemoryFileSystem)
    extends UpdateServer(fs)
    with LockFreeMonitor:
  override val rcu = SchedulableRCU(scheduler)

class SchedulableAtomicLong(initial: Long, scheduler: Scheduler, name: String)
    extends AtomicLong(initial):

  override def get: Long = scheduler.exec {
    super.get
  }(s"", Some(res => s"$name: get $res"))

  override def set(value: Long): Unit = scheduler.exec {
    super.set(value)
  }(s"$name: set $value", None)

  override def incrementAndGet(): Long = scheduler.exec {
    super.incrementAndGet()
  }(s"", Some(res => s"$name: incrementAndGet $res"))

  override def getAndIncrement(): Long = scheduler.exec {
    super.getAndIncrement()
  }(s"", Some(res => s"$name: getandIncrement $res"))

  override def compareAndSet(expected: Long, newValue: Long): Boolean =
    scheduler.exec {
      super.compareAndSet(expected, newValue)
    }(
      s"$name: compareAndSet(expected = $expected, newValue = $newValue)",
      Some(res => s"$name: Did it set? $res")
    )

end SchedulableAtomicLong

class SchedulableAtomicReference[T](
    initial: T,
    scheduler: Scheduler,
    name: String
) extends AtomicReference(initial):
  override def get: T = scheduler.exec {
    super.get
  }(s"", Some(res => s"$name: get $res"))

  override def set(value: T): Unit = scheduler.exec {
    super.set(value)
  }(s"$name: set $value", None)
