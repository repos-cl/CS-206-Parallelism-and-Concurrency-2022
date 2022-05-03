package midterm

import scala.collection.parallel.Task
import scala.collection.parallel.CollectionConverters.*

// Questions 1-3

// See tests in midterm.Part1Test.
// Run with `sbt "testOnly midterm.Part1Test"`.

def parallel3[A, B, C](op1: => A, op2: => B, op3: => C): (A, B, C) =
  val res1 = task { op1 }
  val res2 = task { op2 }
  val res3 = op3
  (res1.join(), res2.join(), res3)

def find(arr: Array[Int], value: Int, threshold: Int): Option[Int] =
  def findHelper(start: Int, end: Int): Option[Int] =
    if end - start <= threshold then
      var i = start
      while i < end do
        if arr(i) == value then return Some(value)
        i += 1
      None
    else
      val inc = (end - start) / 3
      val (res1, res2, res3) = parallel3(
        findHelper(start, start + inc),
        findHelper(start + inc, start + 2 * inc),
        findHelper(start + 2 * inc, end)
      )
      res1.orElse(res2).orElse(res3)
  findHelper(0, arr.size)

def findAggregated(arr: Array[Int], value: Int): Option[Int] =
  val no: Option[Int] = None
  val yes: Option[Int] = Some(value)
  def f = (x1: Option[Int], x2: Int) => if x2 == value then Some(x2) else x1
  def g = (x1: Option[Int], x2: Option[Int]) => if x1 != None then x1 else x2
  arr.par.aggregate(no)(f, g)

@main def part1() =
  println(find(Array(1, 2, 3), 2, 1))

// See tests in Part1Test
