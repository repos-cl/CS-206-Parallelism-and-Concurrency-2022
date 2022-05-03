package midterm

// Questions 4-7

// See tests in midterm.Part2Test.
// Run with `sbt "testOnly midterm.Part2Test"`.

/*
Answers to the exam questions:
  When called with a Vector:
    The total amount of work is O(n), as it is dominated by the time needed to
    read the array. More precisely W(n) = c + 2*W(n/2) = O(n).

    The depth is O(log(n)), because every recursion takes constant time
    and we divide the size of the input by 2 every time, i.e. D(n) = c + D(n/2) = O(log(n)).

    Note however that in practice it is often still faster to manipulate
    start and end indices rather than using take and drop.

  When called with a List:
    Every recursion takes up to time O(n) rather than constant time.

    The total amount of work is O(n) times the number of recursion, because
    take and drop takes time O(n) on lists. Precisely, W(n) = n + 2*W(n/2) = O(log(n)*n)

    The depth is computed similarly: D(n) = n + D(n/2) = O(n), i.e.

Note: these are theoretical results. In practice, you should always double-check
that kind of conclusions with benchmarks. We did so in
`midterm-code/src/main/scala/bench`. Results are available in `bench-results`.
From these results, we can conclude that
1. Vectors are indeed faster in this case, and
2. parallelization of `contains` yields a 2x speedup.
 */
def contains[A](l: Iterable[A], elem: A): Boolean =
  val n = l.size
  if n <= 5 then
    for i <- l do if i == elem then return true
    false
  else
    val (p0, p1) = parallel(
      contains(l.take(n / 2), elem),
      contains(l.drop(n / 2), elem)
    )
    p0 || p1
