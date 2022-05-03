package midterm

import org.junit.*
import org.junit.Assert.*

class Part2Test:
  val testArray2 = Array(0, 50, 7, 1, 28, 42)
  val testList2 = List(0, 50, 7, 1, 28, 42)

  @Test
  def testQuestion4Pos() =
    assert(contains(testArray2, 7))

  @Test
  def testQuestion4Neg() =
    assert(!contains(testArray2, 8))

  @Test
  def testQuestion6Pos() =
    assert(contains(testList2, 7))

  @Test
  def testQuestion6Neg() =
    assert(!contains(testList2, 8))
