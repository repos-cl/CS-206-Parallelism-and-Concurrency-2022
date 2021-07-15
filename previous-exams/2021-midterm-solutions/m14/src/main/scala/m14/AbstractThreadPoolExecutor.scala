package m14

abstract class AbstractThreadPoolExecutor {
  def execute(task: Unit => Unit): Unit
  def start(): Unit
  def shutdown(): Unit
}
