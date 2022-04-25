package m1


trait M1 extends Lib {
  // Functions and classes of Lib can be used in here

  /** Compute the rolling average of array.
   *
   *  For an array `arr = Arr(x1, x2, x3, ..., xn)` the result is
   *  `Arr(x1 / 1, (x1 + x2) / 2, (x1 + x2 + x3) / 3, ..., (x1 + x2 + x3 + ... + xn) / n)`
   */
  def rollingAveragesParallel(arr: Arr[Int]): Arr[Double] = {
    if (arr.length == 0) return Arr.ofLength(0)
    // TASK 1:  Add missing parallelization in `upsweep` and `downsweep`.
    //          You should use the `parallel` method.
    //          You should use the sequential version if the number of elements is lower than THRESHOLD.
    // TASK 2a: Pass `arr` to `upsweep` and `downsweep` instead of `tmp`.
    //          You will need to change some signatures and update the code appropriately.
    //          Remove the definition of `tmp`
    // TASK 2b: Change the type of the array `out` from `Frac` to `Double`
    //          You will need to change some signatures and update the code appropriately.
    //          Remove the call `.map(frac => frac.toDouble)`.
    // TASK 3:  Remove the call to `.tail`.
    //          Update the update the code appropriately.

    val tmp: Arr[Frac] = arr.map(x => Frac(x, 1))
    val out: Arr[Frac] = Arr.ofLength(arr.length + 1)
    val tree = upsweep(tmp, 0, arr.length)
    downsweep(tmp, Frac(0, 0), tree, out)
    out(0) = Frac(0, 0)
    out.map(frac => frac.toDouble).tail

    // IDEAL SOLUTION
    // val out = Arr.ofLength(arr.length)
    // val tree = upsweep(arr, 0, arr.length)
    // downsweep(arr, Frac(0, 0), tree, out)
    // out
  }

  def scanOp(acc: Frac, x: Frac) = // No need to modify this method
    Frac(acc.numerator + x.numerator, acc.denominator + x.denominator)

  def upsweep(input: Arr[Frac], from: Int, to: Int): TreeRes = {
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

  def downsweep(input: Arr[Frac], a0: Frac, tree: TreeRes, output: Arr[Frac]): Unit = {
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

  def downsweepSequential(input: Arr[Frac], from: Int, to: Int, a0: Frac, output: Arr[Frac]): Unit = {
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

  def reduceSequential(input: Arr[Frac], from: Int, to: Int, a0: Frac): Frac = {
    var a = a0
    var i = from
    while (i < to) {
      a = scanOp(a, input(i))
      i = i + 1
    }
    a
  }

}
