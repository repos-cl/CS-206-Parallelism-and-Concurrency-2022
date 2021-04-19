package m14

object M14 {
  /** A thread pool that executes submitted task using one of several threads */
  class ThreadPoolExecutor(taskQueue: BlockingQueue[Unit => Unit], poolSize: Int)
      extends AbstractThreadPoolExecutor {

    private class Worker extends Thread {
      override def run(): Unit = {
        try {
          while (true) {

            val task = taskQueue.take()
            task(())
          }
        } catch {
          case e: InterruptedException =>
            // Nothing to do here, we are shutting down gracefully.
        }
      }
    }
    private val workers: List[Worker] = List.fill(poolSize)(new Worker())

    /** Executes the given task, passed by name. */
    def execute(task: Unit => Unit): Unit =

      taskQueue.put(task)

    /** Starts the thread pool. */
    def start(): Unit =
      workers.foreach(_.start())

    /** Instantly shuts down all actively executing tasks using an interrupt. */
    def shutdown(): Unit =
      workers.foreach(_.interrupt())
  }

  /**
   * A queue whose take operations blocks until the queue become non-empty.
   * Elements must be retrived from this queue in a last in, first out order.
   * All methods of this class are thread safe, that is, they can safely
   * be used from multiple thread without any particular synchronization.
   */
  class BlockingQueue[T] extends AbstractBlockingQueue[T] {

    // The state of this queue is stored in an underlying List[T] defined in
    // the AbstractBlockingQueue class. Your implementation should access and
    // update this list using the following setter and getter methods:
    // - def getUnderlying(): List[T]
    // - def setUnderlying(newValue: List[T]): Unit
    // Using these methods is required for testing purposes.

    /** Inserts the specified element into this queue (non-blocking) */
    def put(elem: T): Unit =

      this.synchronized {
        val underlying = getUnderlying()
        setUnderlying(elem :: underlying)
        this.notifyAll()
      }


    /**
     * Retrieves and removes the head of this queue, waiting if necessary
     * This queue operates in a first in, first out order.
     * until an element becomes available (blocking).
     * This queue operates in a last in, first out order.
     */
    def take(): T =

      this.synchronized {
        while (getUnderlying().isEmpty)
          this.wait()
        val underlying = getUnderlying()
        val head = underlying.head
        setUnderlying(underlying.tail)
        head
      }
  }
}
