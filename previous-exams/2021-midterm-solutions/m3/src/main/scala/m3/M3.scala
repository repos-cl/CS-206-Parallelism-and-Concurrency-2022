package m3


trait M3 extends Lib {
  // Functions and classes of Lib can be used in here

  /** Compute the rolling windowed mean of an array.
   *
   *  For an array `arr = Arr(x1, x2, x3, ..., x_n)` the result is
   *  `Arr(x1, (x1+x2)/2, (x1+x2+x3)/3, (x2+x3+x4)/3, ..., (x_{n-2}, x_{n-1}, x_n)/n)`
   */
  def rollingWinMeanParallel(arr: Arr[Int]): Arr[Double] = {
    if (arr.length == 0) return Arr.ofLength(0)

    val out: Arr[Double] = Arr.ofLength(arr.length)
    val tree = upsweep(arr, 0, arr.length)
    downsweep(arr, AvgWin(Nil), tree, out)
    out
  }

  // No need to modify this
  def scanOp(acc: AvgWin, x: AvgWin) =
    acc.pushAll(x)


  def upsweep(input: Arr[Int], from: Int, to: Int): TreeRes = {
    if (to - from < THRESHOLD)
      Leaf(from, to, reduceSequential(input, from + 1, to, AvgWin(input(from) :: Nil)))
    else {
      val mid = from + (to - from)/2
      val (tL, tR) = parallel(
        upsweep(input, from, mid),
        upsweep(input, mid, to)
      )
      Node(tL, scanOp(tL.res, tR.res), tR)
    }
  }


  def downsweep(input: Arr[Int], a0: AvgWin, tree: TreeRes, output: Arr[Double]): Unit = {
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


  def downsweepSequential(input: Arr[Int], from: Int, to: Int, a0: AvgWin, output: Arr[Double]): Unit = {
    if (from < to) {
      var i = from
      var a = a0
      while (i < to) {
        a = scanOp(a, AvgWin(input(i) :: Nil))
        output(i) = a.toDouble
        i = i + 1
      }
    }
  }


  def reduceSequential(input: Arr[Int], from: Int, to: Int, a0: AvgWin): AvgWin = {
    var a = a0
    var i = from
    while (i < to) {
      a = scanOp(a, AvgWin(input(i) :: Nil))
      i = i + 1
    }
    a
  }
}
