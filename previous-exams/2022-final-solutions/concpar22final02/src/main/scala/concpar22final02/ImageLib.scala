package concpar22final02

import scala.collection.mutable.ArrayBuffer

class ImageLib(size: Int):

  val buffer1: ArrayBuffer[ArrayBuffer[Int]] = ArrayBuffer.fill(size, size)(1)
  val buffer2: ArrayBuffer[ArrayBuffer[Int]] = ArrayBuffer.fill(size, size)(0)

  enum Filter(val kernel: Array[Array[Int]]):
    case Outline extends Filter(Array(Array(-1, -1, -1), Array(-1, 8, -1), Array(-1, -1, -1)))
    case Sharpen extends Filter(Array(Array(0, -1, 0), Array(-1, 5, -1), Array(0, -1, 0)))
    case Emboss extends Filter(Array(Array(-2, -1, 0), Array(-1, 1, 1), Array(0, 1, 2)))
    case Identity extends Filter(Array(Array(0, 0, 0), Array(0, 1, 0), Array(0, 0, 0)))

  def init(input: ArrayBuffer[ArrayBuffer[Int]]) =
    for i <- 0 to size - 1 do
      for j <- 0 to size - 1 do
        buffer1(i)(j) = input(i)(j)

  def computeConvolution(
      kernel: Array[Array[Int]],
      input: ArrayBuffer[ArrayBuffer[Int]],
      row: Int,
      column: Int
  ): Int =

    val displacement = Array(-1, 0, 1)
    var output = 0

    for i <- 0 to 2 do
      for j <- 0 to 2 do
        val newI = row + displacement(i)
        val newJ = column + displacement(j)
        if newI < 0 || newI >= size || newJ < 0 || newJ >= size then output += 0
        else output += (kernel(i)(j) * input(newI)(newJ))

    output

  def applyFilter(
      kernel: Array[Array[Int]],
      input: ArrayBuffer[ArrayBuffer[Int]],
      output: ArrayBuffer[ArrayBuffer[Int]],
      row: Int
  ): Unit =
    for i <- 0 to input(row).size - 1 do
      output(row)(i) = computeConvolution(kernel, input, row, i)
