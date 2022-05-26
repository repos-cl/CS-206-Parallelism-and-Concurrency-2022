package concpar21final03

import scala.annotation.tailrec
import scala.concurrent.*
import scala.concurrent.duration.*
import scala.collection.mutable.HashMap
import scala.util.Random
import instrumentation.*
import instrumentation.TestHelper.*
import instrumentation.TestUtils.*

class Problem3Suite extends munit.FunSuite:

  test("Part 1: ThreadMap (3pts)") {
    testManySchedules(
      4,
      sched =>
        val tmap = new SchedulableThreadMap[Int](sched)

        def writeThread(): Unit =
          tmap.setCurrentThreadValue(0)
          tmap.setCurrentThreadValue(-1)
          val readBack = tmap.currentThreadValue
          assertEquals(readBack, Some(-1))

        def writeAndDeleteThread(): Unit =
          tmap.setCurrentThreadValue(42)
          tmap.deleteCurrentThreadValue()

        @tailrec
        def waitThread(): Unit =
          tmap.waitForall(_ < 0)
          val all = tmap.allValues
          if all != List(-1) then waitThread()

        val threads = List(
          () => writeThread(),
          () => writeAndDeleteThread(),
          () => waitThread(),
          () => waitThread()
        )

        (threads, _ => (true, ""))
    )
  }

  test("Part 2: RCU (5pts)") {
    testManySchedules(
      3,
      sched =>
        val rcu = new SchedulableRCU(sched)

        case class State(
            value: Int,
            isDeleted: AtomicLong = SchedulableAtomicLong(0, sched, "isDeleted")
        )

        val sharedState =
          SchedulableAtomicReference(State(0), sched, "sharedState")

        def readThread(): Unit =
          rcu.startRead()
          val state = sharedState.get
          val stateWasDeleted = state.isDeleted.get != 0
          assert(
            !stateWasDeleted,
            "RCU shared state deleted in the middle of a read."
          )
          rcu.stopRead()

        def writeThread(): Unit =
          val oldState = sharedState.get
          sharedState.set(State(oldState.value + 1))
          rcu.waitForOldReads()
          oldState.isDeleted.set(1)

        val threads = List(
          () => readThread(),
          () => readThread(),
          () => writeThread()
        )

        (threads, _ => (true, ""))
    )
  }

  test("Part 3: UpdateServer (2pts)") {
    testManySchedules(
      3,
      sched =>
        val fs = SchedulableInMemoryFileSystem(sched)
        val server = new SchedulableUpdateServer(sched, fs)

        def writeThread(): Unit =
          server.newUpdate("update1.bin", "Update 1")
          server.newUpdate("update2.bin", "Update 2")
          assertEquals(fs.fsMap.toSet, Set("update2.bin" -> "Update 2"))

        def fetchThread(): Unit =
          val res = server.fetchUpdate()
          assert(
            List(None, Some("Update 1"), Some("Update 2")).contains(res),
            s"fetchUpdate returned unexpected value $res"
          )

        val threads = List(
          () => writeThread(),
          () => fetchThread(),
          () => fetchThread()
        )

        (threads, _ => (true, ""))
    )
  }

  import scala.concurrent.duration.*
  override val munitTimeout = 200.seconds
end Problem3Suite
