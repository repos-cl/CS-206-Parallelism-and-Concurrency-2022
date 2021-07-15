package m21

import instrumentation._

import scala.annotation.tailrec

/** Single-writer, multi-reader data structure containing a pair of integers. */
class SeqCount extends Monitor:
  /** Do not directly use this variable, use `generation` and `setGeneration` instead. */
  @volatile protected var myGeneration: Int = 1
  protected def generation: Int = myGeneration
  protected def setGeneration(newGeneration: Int): Unit =
    myGeneration = newGeneration

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
   *  This method must only be called from one thread at a time.
   */
  final def write(newX: Int, newY: Int): Unit =
    setGeneration(generation + 1)
    setX(newX)
    setY(newY)
    setGeneration(generation + 1)

  /** Copy the values previously written into this data structure into a tuple.
   *  This method is always safe to call.
   *  The implementation of this method is not allowed to call `synchronized`.
   */

  @tailrec
  final def copy(): (Int, Int) =
    val old = generation
    if old % 2 != 1 then
      copy()
    else
      val result = (x, y)
      if generation != old then
        copy()
      else
        result

end SeqCount
