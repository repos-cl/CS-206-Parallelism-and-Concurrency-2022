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
    // TASK 1:  Add missing parallelization in `upsweep` and `downsweep`.
    //          You should use the `parallel` method.
    //          You should use the sequential version if the number of elements is lower than THRESHOLD.
    // TASK 2a: Pass `arr` to `upsweep` and `downsweep` instead of `tmp`.
    //          You will need to change some signatures and update the code appropriately.
    //          Remove the definition of `tmp`
    // TASK 2b: Change the type of the array `out` from `Root` to `Double`
    //          You will need to change some signatures and update the code appropriately.
    //          Remove the call `.map(root => root.toDouble)`.
    // TASK 3:  Remove the call to `.tail`.
    //          Update the update the code appropriately.

    val tmp: Arr[Root] = arr.map(x => Root(x, 1))
    val out: Arr[Root] = Arr.ofLength(arr.length + 1)
    val tree = upsweep(tmp, 0, arr.length)
    downsweep(tmp, Root(1, 0), tree, out)
    out(0) = Root(1, 0)
    out.map(root => root.toDouble).tail

    // IDEAL SOLUTION
    // val out = Arr.ofLength(arr.length)
    // val tree = upsweep(arr, 0, arr.length)
    // downsweep(arr, Root(1, 0), tree, out)
    // out
  }

  def scanOp(acc: Root, x: Root) = // No need to modify this method
    Root(acc.radicand * x.radicand, acc.degree + x.degree)

  def upsweep(input: Arr[Root], from: Int, to: Int): TreeRes = {
    if (to - from < 2)
      Leaf(from, to, reduceSequential(input, from + 1, to, input(from)))
    else {
      val mid = from + (to - from) / 2
      val (tL, tR) = (
        upsweep(input, from, mid),
        upsweep(input, mid, to)
      )
      Node(tL, scanOp(tL.res, tR.res), tR)
    }
  }

  def downsweep(input: Arr[Root], a0: Root, tree: TreeRes, output: Arr[Root]): Unit = {
    tree match {
      case Node(left, _, right) =>
        (
          downsweep(input, a0, left, output),
          downsweep(input, scanOp(a0, left.res), right, output)
        )
      case Leaf(from, to, _) =>
        downsweepSequential(input, from, to, a0, output)
    }
  }

  def downsweepSequential(input: Arr[Root], from: Int, to: Int, a0: Root, output: Arr[Root]): Unit = {
    if (from < to) {
      var i = from
      var a = a0
      while (i < to) {
        a = scanOp(a, input(i))
        i = i + 1
        output(i) = a
      }
    }
  }

  def reduceSequential(input: Arr[Root], from: Int, to: Int, a0: Root): Root = {
    var a = a0
    var i = from
    while (i < to) {
      a = scanOp(a, input(i))
      i = i + 1
    }
    a
  }
}
