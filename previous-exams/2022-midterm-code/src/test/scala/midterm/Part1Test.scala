package midterm

import org.junit.*
import org.junit.Assert.*

class Part1Test:
  val testArray =
    Array(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19)

  @Test
  def testQuestion1Pos() =
    val tasksCreatedBefore = tasksCreated.get
    assertEquals(Some(18), find(testArray, 18, 3))
    assertEquals(10, tasksCreated.get - tasksCreatedBefore)

  @Test
  def testQuestion1Neg() =
    assertEquals(find(testArray, 20, 3), None)

  @Test
  def testQuestion2Pos(): Unit =
    assertEquals(findAggregated(testArray, 18), Some(18))

  @Test
  def testQuestion2Neg(): Unit =
    assertEquals(findAggregated(testArray, 20), None)
