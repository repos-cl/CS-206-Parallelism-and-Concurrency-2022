package concpar22final02

import instrumentation.Monitor

abstract class AbstractBarrier(val numThreads: Int) extends Monitor:

  var count = numThreads

  def awaitZero(): Unit

  def countDown(): Unit
