package concpar22final02.instrumentation

import scala.annotation.tailrec
import concpar22final02.*
import scala.collection.mutable.ArrayBuffer

class SchedulableBarrier(val scheduler: Scheduler, size: Int)
    extends Barrier(size)
    with MockedMonitor

class SchedulableProblem2(
    val scheduler: Scheduler,
    imageSize: Int,
    threadCount: Int,
    numFilters: Int
) extends Problem2(imageSize, threadCount, numFilters):
  self =>

  override val barrier =
    ArrayBuffer.fill(numFilters)(SchedulableBarrier(scheduler, threadCount))
