package m2


trait M2 extends Lib {
  // Functions and classes of Lib can be used in here

  /** Compute the rolling geometric mean of an array.
   *
   *  For an array `arr = Arr(x1, x2, x3, ..., xn)` the result is
   *  `Arr(math.pow(x1, 1), math.pow((x1 + x2), 1.0/2), math.pow((x1 + x2 + x3), 1.0/3), ..., math.pow((x1 + x2 + x3 + ... + xn), 1.0/n))`
   */
  def rollingGeoMeanParallel(arr: Arr[Int]): Arr[Double] = {
    if (arr.length == 0) return Arr.ofLength(0)

    val out: Arr[Double] = Arr.ofLength(arr.length)
    val tree = upsweep(arr, 0, arr.length)
    downsweep(arr, Root(1, 0), tree, out)
    out
  }

  // No need to modify this
  def scanOp(acc: Root, x: Root) =
    Root(acc.radicand * x.radicand, acc.degree + x.degree)


  def upsweep(input: Arr[Int], from: Int, to: Int): TreeRes = {
    if (to - from < THRESHOLD)
      Leaf(from, to, reduceSequential(input, from + 1, to, Root(input(from), 1)))
    else {
      val mid = from + (to - from)/2
      val (tL, tR) = parallel(
        upsweep(input, from, mid),
        upsweep(input, mid, to)
      )
      Node(tL, scanOp(tL.res, tR.res), tR)
    }
  }


  def downsweep(input: Arr[Int], a0: Root, tree: TreeRes, output: Arr[Double]): Unit = {
    tree match {
      case Node(left, _, right) =>
        parallel(
          downsweep(input, a0, left, output),
          downsweep(input, scanOp(a0, left.res), right, output)
        )
      case Leaf(from, to, _) =>
        downsweepSequential(input, from, to, a0, output)
    }
  }


  def downsweepSequential(input: Arr[Int], from: Int, to: Int, a0: Root, output: Arr[Double]): Unit = {
    if (from < to) {
      var i = from
      var a = a0
      while (i < to) {
        a = scanOp(a, Root(input(i), 1))
        output(i) = a.toDouble
        i = i + 1
      }
    }
  }


  def reduceSequential(input: Arr[Int], from: Int, to: Int, a0: Root): Root = {
    var a = a0
    var i = from
    while (i < to) {
      a = scanOp(a, Root(input(i), 1))
      i = i + 1
    }
    a
  }
}
