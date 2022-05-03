package midterm

import org.junit.*
import org.junit.Assert.*
import instrumentation.*

import scala.collection.concurrent.TrieMap
import scala.collection.concurrent.{TrieMap, Map}

class Part8Test:
  @Test
  def usage() =
    val insta = Instagram()
    assertEquals(1, insta.add())
    assertEquals(2, insta.add())
    insta.follow(1, 2)
    assertEquals(insta.graph, Map(1 -> List(2), 2 -> List()))
    insta.follow(2, 1)
    insta.unfollow(1, 2)
    assertEquals(insta.graph, Map(1 -> List(), 2 -> List(1)))
    insta.follow(3, 1) // fails silently
    assertEquals(insta.graph, Map(1 -> List(), 2 -> List(1)))
    insta.remove(1)
    assertEquals(insta.graph, Map(2 -> List()))
    insta.unfollow(1, 2) // fails silently

  @Test
  def testParallelFollowABRemoveA() =
    TestHelper.testManySchedules(
      2,
      scheduler =>
        val insta = new Instagram:
          override val graph =
            ScheduledTrieMap(TrieMap[Int, List[Int]](), scheduler)

        val u1 = insta.add()
        val u2 = insta.add()

        (
          List(
            () =>
              // Thread 1
              insta.follow(u1, u2),
            () =>
              // Thread 2
              insta.remove(u1)
          ),
          results =>
            val size = insta.graph.size
            if size != 1 then
              (false, f"Wrong number of user: expected 1 but got ${size}")
            else validateGraph(insta)
        )
    )

  @Test
  def testParallelFollowABRemoveB() =
    TestHelper.testManySchedules(
      2,
      scheduler =>
        val insta = new Instagram:
          override val graph =
            ScheduledTrieMap(TrieMap[Int, List[Int]](), scheduler)

        val u1 = insta.add()
        val u2 = insta.add()

        (
          List(
            () =>
              // Thread 1
              insta.follow(u1, u2),
            () =>
              // Thread 2
              insta.remove(u2)
          ),
          results =>
            val size = insta.graph.size
            if size != 1 then
              (false, f"Wrong number of user: expected 1 but got ${size}")
            else validateGraph(insta)
        )
    )

  @Test
  def testParallelFollowACRemoveB() =
    TestHelper.testManySchedules(
      2,
      scheduler =>
        val insta = new Instagram:
          override val graph =
            ScheduledTrieMap(TrieMap[Int, List[Int]](), scheduler)

        val u1 = insta.add()
        val u2 = insta.add()
        val u3 = insta.add()
        insta.follow(u1, u2)

        (
          List(
            () =>
              // Thread 1
              insta.follow(u1, u3),
            () =>
              // Thread 2
              insta.remove(u2)
          ),
          results =>
            val size = insta.graph.size
            if size != 2 then
              (false, f"Wrong number of user: expected 2 but got ${size}")
            else validateGraph(insta)
        )
    )

  @Test
  def testParallelFollow() =
    TestHelper.testManySchedules(
      2,
      scheduler =>
        val insta = new Instagram:
          override val graph =
            ScheduledTrieMap(TrieMap[Int, List[Int]](), scheduler)

        val u1 = insta.add()
        val u2 = insta.add()
        val u3 = insta.add()

        (
          List(
            () =>
              // Thread 1
              insta.follow(u1, u2),
            () =>
              // Thread 2
              insta.follow(u1, u3)
          ),
          results =>
            val u1FollowingSize = insta.graph(u1).size
            if u1FollowingSize != 2 then
              (
                false,
                f"Wrong number of users followed by user 1: expected 2 but got ${u1FollowingSize}"
              )
            else validateGraph(insta)
        )
    )
  
  @Test
  def testParallelRemove() =
    TestHelper.testManySchedules(
      2,
      scheduler =>
        val insta = new Instagram:
          override val graph =
            ScheduledTrieMap(TrieMap[Int, List[Int]](), scheduler)

        // Setup
        val u1 = insta.add()
        val u2 = insta.add()
        val u3 = insta.add()
        insta.follow(u1, u2)
        insta.follow(u2, u1)
        insta.follow(u2, u3)
        insta.follow(u3, u1)

        (
          List(
            () =>
              // Thread 1
              insta.remove(u2),
            () =>
              // Thread 2
              insta.remove(u3)
          ),
          results =>
            val size = insta.graph.size
            if size != 1 then
              (false, f"Wrong number of user: expected 1 but got ${size}")
            else validateGraph(insta)
        )
    )
  
  // We test wrong code here, so we expect an assertion error. You can replace
  // the next line by `@Test` if you want to see the error with the failing
  // schedule.
  @Test(expected = classOf[AssertionError])
  def testParallelWrongAdd() =
    TestHelper.testManySchedules(
      2,
      scheduler =>
        val insta = new Instagram:
          override val graph =
            ScheduledTrieMap(TrieMap[Int, List[Int]](), scheduler)
          
          // This implementation of `add` is wrong, because two threads might
          // allocate the same id.
          // Consider the following schedule:
          // T1: res = 1
          // T2: res = 2
          // T2: graph.update(2, Nil)
          // T2: 2
          // T1: graph.update(2, Nil)
          // T1: 2
          override def add(): Int =
            val res = maxId.incrementAndGet
            graph.update(maxId.get, Nil)
            res

        (
          List(
            () =>
              // Thread 1
              insta.add(),
            () =>
              // Thread 2
              insta.add()
          ),
          results =>
            if results(0) != results(1) then
              (false, f"Allocated twice id ${results(0)}")
            else validateGraph(insta)
        )
    )

  // We test wrong code here, so we expect an assertion error. You can replace
  // the next line by `@Test` if you want to see the error with the failing
  // schedule.
  @Test(expected = classOf[AssertionError])
  def testParallelWrongRemove() =
    TestHelper.testManySchedules(
      2,
      scheduler =>
        val insta = new Instagram:
          override val graph =
            ScheduledTrieMap(TrieMap[Int, List[Int]](), scheduler)

          // This implementation of `remove` is wrong because we don't retry to
          // call `graph.replace` when it fails. Therefore, user 1 might end up
          // following user 2 that has been removed, or not following user 3
          // which is concurrently followed.
          override def remove(idToRemove: Int): Unit =
            graph.remove(idToRemove)
            for (key, value) <- graph do
              graph.replace(key, value, value.filter(_ != idToRemove))
              // Note: writing `graph(key) = value.filter(_ != idToRemove)`
              // would also be wrong because it does not check the previous
              // value. Therefore, it could erase a concurrent update.

        val u1 = insta.add()
        val u2 = insta.add()
        val u3 = insta.add()
        insta.follow(u1, u2)

        (
          List(
            () =>
              // Thread 1
              insta.follow(u1, u3),
            () =>
              // Thread 2
              insta.remove(u2)
          ),
          results =>
            val size = insta.graph.size
            if insta.graph(u1).size != 1 then
              (false, f"Wrong number of users followed by 1: expected 1 but got ${insta.graph(u1)}")
            else validateGraph(insta)
        )
    )

  // We test wrong code here, so we expect an assertion error. You can replace
  // the next line by `@Test` if you want to see the error with the failing
  // schedule.
  @Test(expected = classOf[AssertionError])
  def testParallelWrongUnfollow() =
    TestHelper.testManySchedules(
      2,
      scheduler =>
        val insta = new Instagram:
          override val graph =
            ScheduledTrieMap(TrieMap[Int, List[Int]](), scheduler)
          override def unfollow(a: Int, b: Int): Unit =
            if !graph.contains(a) then return
            val prev = graph(a) // Might throw java.util.NoSuchElementException
            if !graph.replace(a, prev, prev.filter(_ != b)) then unfollow(a, b)

        val u1 = insta.add()
        val u2 = insta.add()
        insta.follow(u1, u2)

        (
          List(
            () =>
              // Thread 1
              insta.unfollow(u1, u2),
            () =>
              // Thread 2
              insta.remove(u1)
          ),
          results =>
            val size = insta.graph.size
            if size != 1 then
              (false, f"Wrong number of user: expected 1 but got ${size}")
            else validateGraph(insta)
        )
    )
  
  def validateGraph(insta: Instagram): (Boolean, String) =
    for (a, following) <- insta.graph; b <- following do
      if !insta.graph.contains(b) then
        return (false, f"User $a follows non-existing user $b")
    (true, "")

  final class ScheduledIterator[T](
      private val myIterator: Iterator[T],
      private val scheduler: Scheduler
  ) extends Iterator[T]:
    override def hasNext =
      myIterator.hasNext
    override def next() =
      scheduler.exec(myIterator.next)("", Some(res => f"Iterator.next == $res"))
    override def knownSize: Int =
      myIterator.knownSize

  final class ScheduledTrieMap[K, V](
      private val myMap: Map[K, V],
      private val scheduler: Scheduler
  ) extends Map[K, V]:
    override def apply(key: K): V =
      scheduler.exec(myMap(key))(
        "",
        Some(res => f"TrieMap.apply($key) == $res")
      )
    override def contains(key: K): Boolean =
      scheduler.exec(myMap.contains(key))(
        "",
        Some(res => f"TrieMap.contains($key) == $res")
      )
    override def get(key: K): Option[V] =
      scheduler.exec(myMap.get(key))(
        "",
        Some(res => f"TrieMap.get($key) == $res")
      )
    override def addOne(kv: (K, V)) =
      scheduler.exec(myMap.addOne(kv))(f"TrieMap.addOne($kv)")
      this
    override def subtractOne(k: K) =
      scheduler.exec(myMap.subtractOne(k))(f"TrieMap.subtractOne($k)")
      this
    override def iterator() =
      scheduler.log("TrieMap.iterator")
      ScheduledIterator(myMap.iterator, scheduler)
    override def replace(k: K, v: V): Option[V] =
      scheduler.exec(myMap.replace(k, v))(
        "",
        Some(res => f"TrieMap.replace($k, $v) == $res")
      )
    override def replace(k: K, oldvalue: V, newvalue: V): Boolean =
      scheduler.exec(myMap.replace(k, oldvalue, newvalue))(
        "",
        Some(res => f"TrieMap.replace($k, $oldvalue, $newvalue) == $res")
      )
    override def putIfAbsent(k: K, v: V): Option[V] =
      scheduler.exec(myMap.putIfAbsent(k, v))(
        "",
        Some(res => f"TrieMap.putIfAbsent($k, $v)")
      )
    override def remove(k: K): Option[V] =
      scheduler.exec(myMap.remove(k))(
        "",
        Some(res => f"TrieMap.remove($k)")
      )
    override def remove(k: K, v: V): Boolean =
      scheduler.exec(myMap.remove(k, v))(
        "",
        Some(res => f"TrieMap.remove($k, $v)")
      )
