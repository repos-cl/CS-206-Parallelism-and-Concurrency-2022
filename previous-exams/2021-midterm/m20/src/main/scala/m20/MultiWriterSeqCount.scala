package m20

import instrumentation._

import scala.annotation.tailrec

/** Multi-writer, multi-reader data structure containing a pair of integers. */
class MultiWriterSeqCount extends Monitor:
  /** Do not directly use this variable, use `generation`, `setGeneration` and
   *  `compareAndSetGeneration` instead.
   */
  protected val myGeneration: AbstractAtomicVariable[Int] = new AtomicVariable(0)
  protected def generation: Int = myGeneration.get
  protected def setGeneration(newGeneration: Int): Unit =
    myGeneration.set(newGeneration)
  protected def compareAndSetGeneration(expected: Int, newValue: Int): Boolean =
    myGeneration.compareAndSet(expected, newValue)

  /** Do not directly use this variable, use `x` and `setX` instead. */
  protected var myX: Int = 0
  protected def x: Int = myX
  protected def setX(newX: Int): Unit =
    myX = newX

  /** Do not directly use this variable, use `y` and `setY` instead. */
  protected var myY: Int = 0
  protected def y: Int = myY
  protected def setY(newY: Int): Unit =
    myY = newY

  /** Write new values into this data structure.
   *  This method is always safe to call.
   *  The implementation of this method is not allowed to call `synchronized`.
   */
  final def write(newX: Int, newY: Int): Unit = ???

  /** Copy the values previously written into this data structure into a tuple.
   *  This method is always safe to call.
   *  The implementation of this method is not allowed to call `synchronized`.
   */
  final def copy(): (Int, Int) =
    // You should be able to just copy-paste the implementation of `copy` you
    // wrote in `SeqCount` here.
    ???

end MultiWriterSeqCount
