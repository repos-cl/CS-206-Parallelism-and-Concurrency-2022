package m2

////////////////////////////////////////
// NO NEED TO MODIFY THIS SOURCE FILE //
////////////////////////////////////////

trait Lib {

  /** If an array has `n` elements and `n < THRESHOLD`, then it should be processed sequentially */
  final val THRESHOLD: Int = 33

  /** Compute the two values in parallel
   *
   *  Note: Most tests just compute those two sequentially to make any bug simpler to debug
   */
  def parallel[T1, T2](op1: => T1, op2: => T2): (T1, T2)

  /** A limited array. It only contains the required operations for this exercise. */
  trait Arr[T] {
    /** Get the i-th element of the array (0-based) */
    def apply(i: Int): T
    /** Update the i-th element of the array with the given value (0-based) */
    def update(i: Int, x: T): Unit
    /** Number of elements in this array */
    def length: Int
    /** Create a copy of this array without the first element */
    def tail: Arr[T]
    /** Create a copy of this array by mapping all the elements with the given function */
    def map[U](f: T => U): Arr[U]
  }

  object Arr {
    /** Create an array with the given elements */
    def apply[T](xs: T*): Arr[T] = {
      val arr: Arr[T] = Arr.ofLength(xs.length)
      for i <- 0 until xs.length do arr(i) = xs(i)
      arr
    }

    /** Create an array with the given length. All elements are initialized to `null`. */
    def ofLength[T](n: Int): Arr[T] =
      newArrOfLength(n)

  }

  /** Create an array with the given length. All elements are initialized to `null`. */
  def newArrOfLength[T](n: Int): Arr[T]

  /** A number representing `radicand^(1.0/degree)` */
  case class Root(radicand: Int, degree: Int) {
    def toDouble: Double = scala.math.pow(radicand, 1.0/degree)
  }

  /** Tree result of an upsweep operation. Specialized for `Root` results. */
  trait TreeRes { val res: Root }
  /** Leaf result of an upsweep operation. Specialized for `Root` results. */
  case class Leaf(from: Int, to: Int, res: Root) extends TreeRes
  /** Tree node result of an upsweep operation. Specialized for `Root` results. */
  case class Node(left: TreeRes, res: Root, right: TreeRes) extends TreeRes
}
