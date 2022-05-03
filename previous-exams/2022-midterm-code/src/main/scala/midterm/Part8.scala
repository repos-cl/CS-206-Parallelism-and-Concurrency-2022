package midterm

import scala.collection.concurrent.{TrieMap, Map}
import java.util.concurrent.atomic.AtomicInteger
import scala.annotation.tailrec

// Question 25

// See tests in midterm.Part8Test.
// Run with `sbt "testOnly midterm.Part8Test"`.

// Represent a social network where user can follow each other. Each user is
// represented by an id that is an `Int`.
abstract class AbstractInstagram:
  // The map storing the "following" relation of our social network.
  // `graph(a)` contains the list of user ids that user `a` follows.
  val graph: Map[Int, List[Int]] = new TrieMap[Int, List[Int]]()

  // The maximum user id allocated until now. This value should be incremented
  // by one each time a new user is added.
  val maxId = new AtomicInteger(0)

  // Allocates a new user and returns its unique id. Internally, this should
  // also create an empty list at the corresponding id in `graph`. The
  // implementation must be thread-safe.
  def add(): Int

  // Make `a` follow `b`. The implementation must be thread-safe.
  def follow(a: Int, b: Int): Unit

  // Makes `a` unfollow `b`. The implementation must be thread-safe.
  def unfollow(a: Int, b: Int): Unit

  // Removes user with id `a`. This should also remove all references to `a`
  // in `graph`. The implementation must be thread-safe.
  def remove(a: Int): Unit

class Instagram extends AbstractInstagram:
  // This method is worth 6 points.
  def add(): Int =
    // It is important to increment and read the value in one atomic step. See
    // test `testParallelWrongAdd` for an alternative wrong implementation.
    val id = maxId.incrementAndGet
    // Note: it is also correct to use `graph.putIfAbsent`, but not needed as
    // `id` is always new and therefore absent from the map at this point.
    graph.update(id, Nil)
    id

  // This method is worth 8 points.
  def remove(a: Int): Unit =
    graph.remove(a)
    // Iterate through all keys to make sure that nobody follows `a` anymore.
    // For each key, we need to unfollow a in a thread-safe manner. Calling
    // `unfollow` is the simplest way to so, as it is already guaranteed to be
    // thread-safe. See test `testParallelWrongRemove` for an example of wrong
    // implementation.
    for b <- graph.keys do unfollow(b, a)

  // This method is worth 10 points.
  def unfollow(a: Int, b: Int) =
    // Here, it is important to read the value only once. First calling
    // `.contains(a)` and then `graph(a)` (or `graph.apply(a)`--which is the
    // same thing) does not work because `a` might be removed between the two
    // calls. See `testParallelWrongUnfollow` for an example of this wrong
    // implementation.
    val prev = graph.get(a)
    // Returns silently if `a` does not exist.
    if prev.isEmpty then return
    // We replace the list of users that `a` follows in an atomic manner. If the
    // list of followed users changed concurrently, we start over.
    if !graph.replace(a, prev.get, prev.get.filter(_ != b)) then unfollow(a, b)

  // This method is worth 12 points.
  def follow(a: Int, b: Int) =
    val prev = graph.get(a)
    // Returns silently if `a` does not exist.
    if prev.isEmpty then return
    // We replace the list of users that `a` follows in an atomic manner. If the
    // list of followed users changed concurrently, we start over.
    if !graph.replace(a, prev.get, b :: prev.get) then follow(a, b)
    // Difficult: this handles the case where `b` is concurrently removed by
    // another thread. To detect this case, we must check if `b` still exists
    // after we have followed it, and unfollow it if it is not the case. See
    // test `testParallelFollowABRemoveB`. This last step is worth 4 points.
    else if !graph.contains(b) then unfollow(a, b)
