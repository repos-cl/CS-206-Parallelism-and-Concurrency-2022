package m14

import instrumentation.SchedulableBlockingQueue
import instrumentation.TestHelper._
import instrumentation.TestUtils._

class M14Suite extends munit.FunSuite {
  import M14._

  test("ThreadPool should put jobs in the queue, Workers should execute jobs from the queue (10pts)") {
    case class PutE(e: Unit => Unit) extends Exception
    val nThreads = 3
    var taken = false
    class TestBlockingQueue extends BlockingQueue[Unit => Unit] {
      override def put(e: Unit => Unit): Unit =
        throw new PutE(e)

      override def take(): Unit => Unit =
        x => {
          taken = true
          Thread.sleep(10 * 1000)
        }
    }

    val tpe = new ThreadPoolExecutor(new TestBlockingQueue, nThreads)
    val unit2unit: Unit => Unit = x => ()
    try {
      tpe.execute(unit2unit)
      assert(false, "ThreadPoolExecutor does not put jobs in the queue")
    } catch {
      case PutE(e) =>
        assert(e == unit2unit)
    }
    tpe.start()
    Thread.sleep(1000)
    assert(taken, s"ThreadPoolExecutor workers do no execute jobs from the queue")
    tpe.shutdown()
  }

  test("BlockingQueue should work in a sequential setting (1pts)") {
    testSequential[(Int, Int, Int, Int)]{ sched =>
      val queue = new SchedulableBlockingQueue[Int](sched)
      queue.put(1)
      queue.put(2)
      queue.put(3)
      queue.put(4)
      (queue.take(),
      queue.take(),
      queue.take(),
      queue.take())
    }{ tuple =>
      (tuple == (4, 3, 2, 1), s"Expected (4, 3, 2, 1) got $tuple")
    }
  }

  test("BlockingQueue should work when Thread 1: 'put(1)', Thread 2: 'take' (3pts)") {
    testManySchedules(2, sched => {
      val queue = new SchedulableBlockingQueue[Int](sched)
      (List(() => queue.put(1), () => queue.take()),
       args => (args(1) == 1, s"Expected 1, got ${args(1)}"))
    })
  }

  test("BlockingQueue should not be able to take from an empty queue (3pts)") {
    testSequential[Boolean]{ sched =>
      val queue = new SchedulableBlockingQueue[Int](sched);
      queue.put(1)
      queue.put(2)
      queue.take()
      queue.take()
      failsOrTimesOut(queue.take())
    }{ res =>
      (res, "Was able to retrieve an element from an empty queue")
    }
  }

  test("BlockingQueue should work when Thread 1: 'put(1)', Thread 2: 'put(2)', Thread 3: 'take' (5pts)") {
    testManySchedules(3, sched => {
      val queue = new SchedulableBlockingQueue[Int](sched)
      (List(() => queue.put(1), () => queue.put(2), () => queue.take())
      , args => {
        val takeRes = args(2).asInstanceOf[Int]
        val nocreation = (takeRes == 1 || takeRes == 2)
        if (!nocreation)
          (false, s"'take' should return either 1 or 2")
        else (true, "")
      })
    })
  }

  test("BlockingQueue should work when Thread 1: 'put(1)', Thread 2: 'put(2)', Thread 3: 'take', Thread 4: 'take' (10pts)") {
    testManySchedules(4, sched => {
      val queue = new SchedulableBlockingQueue[Int](sched)
      (List(() => queue.put(1), () => queue.put(2), () => queue.take(), () => queue.take())
      , args => {
        def m(): (Boolean, String) = {
          val takeRes1 = args(2).asInstanceOf[Int]
          val takeRes2 = args(3).asInstanceOf[Int]
          val nocreation = (x: Int) => List(1, 2).contains(x)
          if (!nocreation(takeRes1))
            return (false, s"'Thread 3: take' returned $takeRes1 but should return a value in {1, 2, 3}")
          if (!nocreation(takeRes2))
            return (false, s"'Thread 4: take' returned $takeRes2 but should return a value in {1, 2, 3}")

          val noduplication = takeRes1 != takeRes2
          if (!noduplication)
            (false, s"'Thread 3 and 4' returned the same value: $takeRes1")
          else (true, "")
        }
        m()
      })
    })
  }

  test("BlockingQueue should work when Thread 1: 'put(1)', Thread 2: 'put(2)', Thread 3: 'put(3)', Thread 4: 'take', Thread 5: 'take' (10pts)") {
    testManySchedules(5, sched => {
      val queue = new SchedulableBlockingQueue[Int](sched)
      (List(() => queue.put(1), () => queue.put(2), () => queue.put(3),
        () => queue.take(), () => queue.take())
      , args => {
        def m(): (Boolean, String) = {
          val takeRes1 = args(3).asInstanceOf[Int]
          val takeRes2 = args(4).asInstanceOf[Int]
          val nocreation = (x: Int) => List(1, 2, 3).contains(x)
          if (!nocreation(takeRes1))
            return (false, s"'Thread 4: take' returned $takeRes1 but should return a value in {1, 2, 3}")
          if (!nocreation(takeRes2))
            return (false, s"'Thread 5: take' returned $takeRes2 but should return a value in {1, 2, 3}")

          val noduplication = takeRes1 != takeRes2
          if (!noduplication)
            return (false, s"'Thread 4 and 5' returned the same value: $takeRes1")
          else (true, "")
        }
        m()
      })
    })
  }

  test("BlockingQueue should work when Thread 1: 'put(1); put(2); take', Thread 2: 'put(3)', Thread 3: 'put(4)' (10pts)") {
    testManySchedules(3, sched => {
      val queue = new SchedulableBlockingQueue[Int](sched)
      (List(
        () => { queue.put(1); queue.put(2); queue.take() },
        () => queue.put(3),
        () => queue.put(4)
      ), args => {
        val takeRes = args(0).asInstanceOf[Int]
        val nocreation = List(1, 2, 3, 4).contains
        if (!nocreation(takeRes))
          (false, s"'Thread 1: take' returned $takeRes, but should return a value in {1, 2, 3, 4}")
        else if (takeRes == 1)
          (false, s"'Thread 1' returned 2 before returning 1 (got $takeRes)")
        else
          (true, "")
      })
    })
  }
}
