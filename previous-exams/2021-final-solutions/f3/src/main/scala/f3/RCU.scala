package f3

import instrumentation._

/** A synchronization mechanism allowing multiple reads to proceed concurrently
 *  with an update to the state.
 */
class RCU extends Monitor:
  protected val latestVersion: AtomicLong = AtomicLong(0)
  protected val readersVersion: ThreadMap[Long] = ThreadMap()

  /** This method must be called before accessing shared data for reading. */
  def startRead(): Unit =
    assert(!readersVersion.currentThreadHasValue,
      "startRead() cannot be called multiple times without an intervening stopRead()")
    readersVersion.setCurrentThreadValue(latestVersion.get)

  /** Once a thread which has previously called `startRead` has finished reading
   *  shared data, it must call this method.
   */
  def stopRead(): Unit =
    assert(readersVersion.currentThreadHasValue,
      "stopRead() cannot be called without a preceding startRead()")
    readersVersion.deleteCurrentThreadValue()

  /** Wait until all reads started before this method was called have finished,
   *  then return.
   */
  def waitForOldReads(): Unit =

    val newVersion = latestVersion.incrementAndGet()
    readersVersion.waitForall(_ >= newVersion)
