package f3

import instrumentation._

import scala.collection.mutable

/** A map which associates every thread to at most one value of type A.
 *
 *  Every method in this class is thread-safe.
 */
class ThreadMap[A] extends Monitor:
  protected val theMap: mutable.Map[Thread, A] = mutable.Map()

  /** Return the value in the map entry for the current thread if it exists,
   *  otherwise None. */
  def currentThreadValue: Option[A] = synchronized {
    theMap.get(Thread.currentThread)
  }

  /** Is there a map entry for the current thread? */
  def currentThreadHasValue: Boolean =

    synchronized {
      theMap.contains(Thread.currentThread)
    }

  /** Set the map entry of the current thread to `value` and notify any thread
   *  waiting on `waitForall`. */
  def setCurrentThreadValue(value: A): Unit =

    synchronized {
      theMap(Thread.currentThread) = value
      notifyAll()
    }

  /** Delete the map entry associated with this thread (if it exists) and notify
   *  all threads waiting in `waitForall`. */
  def deleteCurrentThreadValue(): Unit =

    synchronized {
      theMap.remove(Thread.currentThread)
      notifyAll()
    }

  /** Wait until `predicate` returns true for all map entries, then return. */
  def waitForall(predicate: A => Boolean): Unit =

    synchronized {
      while !theMap.forall((_, value) => predicate(value)) do
        wait()
    }

end ThreadMap
