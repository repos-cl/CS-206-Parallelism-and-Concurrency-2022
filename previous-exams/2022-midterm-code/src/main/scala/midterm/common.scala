package midterm

import java.util.concurrent.ForkJoinTask
import java.util.concurrent.RecursiveTask
import java.util.concurrent.ForkJoinWorkerThread
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicInteger

val forkJoinPool = ForkJoinPool()
var parallelismEnabled = true
var tasksCreated: AtomicInteger = AtomicInteger(0)

def schedule[T](body: => T): ForkJoinTask[T] =
  val t = new RecursiveTask[T]:
    def compute = body
  Thread.currentThread match
    case wt: ForkJoinWorkerThread => t.fork()
    case _                        => forkJoinPool.execute(t)
  t

def task[T](body: => T): ForkJoinTask[T] =
  tasksCreated.incrementAndGet
  schedule(body)

def parallel[A, B](op1: => A, op2: => B): (A, B) =
  if parallelismEnabled then
    val res1 = task { op1 }
    val res2 = op2
    (res1.join(), res2)
  else (op1, op2)
