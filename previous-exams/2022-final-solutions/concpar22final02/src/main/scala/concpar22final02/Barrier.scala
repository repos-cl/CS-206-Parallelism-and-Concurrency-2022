package concpar22final02

class Barrier(numThreads: Int) extends AbstractBarrier(numThreads):


  def awaitZero(): Unit =
    synchronized {
      while count > 0 do wait()
    }


  def countDown(): Unit =
    synchronized {
      count -= 1
      if count <= 0 then notifyAll()
    }
