package m15

import instrumentation.SchedulableBlockingQueue
import instrumentation.TestHelper._
import instrumentation.TestUtils._

class M15Suite extends munit.FunSuite {
  import M15._

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
      (tuple == (1, 2, 3, 4), s"Expected (1, 2, 3, 4) got $tuple")
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

  test("Should work when Thread 1: 'put(1)', Thread 2: 'put(2)', Thread 3: 'take', and a buffer of size 1") {
    testManySchedules(3, sched => {
      val prodCons = new SchedulableBlockingQueue[Int](sched)
      (List(() => prodCons.put(1), () => prodCons.put(2), () => prodCons.take())
      , args => {
        val takeRes = args(2).asInstanceOf[Int]
        val nocreation = (takeRes == 1 || takeRes == 2)
        if (!nocreation)
          (false, s"'take' should return either 1 or 2")
        else (true, "")
      })
    })
  }

  // testing no duplication
  test("Should work when Thread 1: 'put(1)', Thread 2: 'put(2)', Thread 3: 'take', Thread 4: 'take', and a buffer of size 3") {
    testManySchedules(4, sched => {
      val prodCons = new SchedulableBlockingQueue[Int](sched)
      (List(() => prodCons.put(1), () => prodCons.put(2), () => prodCons.take(), () => prodCons.take())
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

  // testing no duplication with 5 threads
  test("Should work when Thread 1: 'put(1)', Thread 2: 'put(2)', Thread 3: 'put(3)', Thread 4: 'take', Thread 5: 'take', and a buffer of size 1") {
    testManySchedules(5, sched => {
      val prodCons = new SchedulableBlockingQueue[Int](sched)
      (List(() => prodCons.put(1), () => prodCons.put(2), () => prodCons.put(3),
        () => prodCons.take(), () => prodCons.take())
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

  // testing fifo buffer size 1
  test("Should work when Thread 1: 'put(1); put(2)', Thread 2: 'take', Thread 3: 'put(3)', Thread 4: 'put(4)', and a buffer of size 3") {
    testManySchedules(4, sched => {
      val prodCons = new SchedulableBlockingQueue[Int](sched)
      (List(() => { prodCons.put(1); prodCons.put(2) }, () => prodCons.take(),
        () => prodCons.put(3), () => prodCons.put(4))
      , args => {
        def m(): (Boolean, String) = {
          val takeRes = args(1).asInstanceOf[Int]
          // no creation
          val nocreation = (x: Int) => List(1, 2, 3, 4).contains(x)
          if (!nocreation(takeRes))
            return (false, s"'Thread 2: take' returned $takeRes, but should return a value in {1, 2, 3, 4}")
          // fifo (cannot have 2 without 1)
          if (takeRes == 2)
            (false, s"'Thread 2' returned 2 before returning 1")
          else
            (true, "")
        }
        m()
      })
    })
  }

  // testing fifo buffer size 5
  test("Should work when Thread 1: 'put(1); put(2)', Thread 2: 'take', Thread 3: 'put(11)', Thread 4: 'put(10)', and a buffer of size 5") {
    testManySchedules(4, sched => {
      val prodCons = new SchedulableBlockingQueue[Int](sched)
      (List(() => { prodCons.put(1); prodCons.put(2) }, () => prodCons.take(),
        () => prodCons.put(11), () => prodCons.put(10))
      , args => {
        def m(): (Boolean, String) = {
          val takeRes = args(1).asInstanceOf[Int]
          // no creation
          val nocreation = (x: Int) => List(1, 2, 10, 11).contains(x)
          if (!nocreation(takeRes))
            return (false, s"'Thread 2: take' returned $takeRes, but should return a value in {1, 2, 10, 11}")
          // fifo (cannot have 2 without 1)
          if (takeRes == 2)
            (false, s"'Thread 2' returned 2 before returning 1")
          else
            (true, "")
        }
        m()
      })
    })
  }

  // testing fifo on more complicated case
  test("Should work when Thread 1: 'put(1); put(3)', Thread 2: 'put(2)', Thread 3: 'put(4)', Thread 4: 'take', Thread 5: 'take', and a buffer of size 10") {
    testManySchedules(5, sched => {
      val prodCons = new SchedulableBlockingQueue[Int](sched)
      (List(() => { prodCons.put(1); prodCons.put(3) }, () => prodCons.put(2),
        () => prodCons.put(4), () => prodCons.take(), () => prodCons.take())
      , args => {
        def m(): (Boolean, String) = {
          val takeRes1 = args(3).asInstanceOf[Int]
          val takeRes2 = args(4).asInstanceOf[Int]
          // no creation
          val nocreation = (x: Int) => List(1, 2, 3, 4).contains(x)
          if (!nocreation(takeRes1))
            return (false, s"'Thread 4: take' returned $takeRes1 but should return a value in {1, 2, 3, 4}")
          if (!nocreation(takeRes2))
            return (false, s"'Thread 5: take' returned $takeRes2 but should return a value in {1, 2, 3, 4}")
          // no duplication
          if (takeRes1 == takeRes2)
            return (false, s"'Thread 4 and 5' returned the same value: $takeRes1")
          // fifo (cannot have 3 without 1)
          val takes = List(takeRes1, takeRes2)
          if (takes.contains(3) && !takes.contains(1))
            (false, s"'Thread 4 or 5' returned 3 before returning 1")
          else
            (true, "")
        }
        m()
      })
    })
  }

  // combining put and take in one thread
  test("Should work when Thread 1: 'put(21); put(22)', Thread 2: 'take', Thread 3: 'put(23); take', Thread 4: 'put(24); take', and a buffer of size 2") {
    testManySchedules(4, sched => {
      val prodCons = new SchedulableBlockingQueue[Int](sched)
      (List(() => { prodCons.put(21); prodCons.put(22) }, () => prodCons.take(),
        () => { prodCons.put(23); prodCons.take() }, () => { prodCons.put(24); prodCons.take() })
      , args => {
        def m(): (Boolean, String) = {
          val takes = List(args(1).asInstanceOf[Int], args(2).asInstanceOf[Int], args(3).asInstanceOf[Int])
          // no creation
          val vals = List(21, 22, 23, 24)

          var i = 0
          while (i < takes.length) {
            val x = takes(i)
            if (!vals.contains(x))
              return (false, s"'Thread $i: take' returned $x but should return a value in $vals")
            i += 1
          }

          // no duplication
          if (takes.distinct.size != takes.size)
            return (false, s"Takes did not return unique values: $takes")
          // fifo (cannot have 22 without 21)
          if (takes.contains(22) && !takes.contains(21))
            (false, s"`Takes returned 22 before returning 21")
          else
            (true, "")
        }
        m()
      })
    })
  }

  // completely hidden hard to crack test
  test("[Black box test] Values should be taken in the order they are put") {
    testManySchedules(4, sched => {
      val prodCons = new SchedulableBlockingQueue[(Char, Int)](sched)
      val n = 2
      (List(
        () => for (i <- 1 to n) { prodCons.put(('a', i)) },
        () => for (i <- 1 to n) { prodCons.put(('b', i)) },
        () => for (i <- 1 to n) { prodCons.put(('c', i)) },
        () => {
          import scala.collection.mutable
          var counts = mutable.HashMap.empty[Char, Int]
          counts('a') = 0
          counts('b') = 0
          counts('c') = 0
          for (i <- 1 to (3 * n)) {
            val (c, n) = prodCons.take()
            counts(c) += 1
            assert(counts(c) == n)
          }
        })
      , _ =>
        (true, "")
      )
    })
  }
}
