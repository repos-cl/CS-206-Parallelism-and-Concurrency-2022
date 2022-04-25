package m7

import java.util.concurrent._
import scala.util.DynamicVariable

trait M7 extends Lib {

  class DLLCombinerImplementation(chunk_size: Int = 3) extends DLLCombiner(chunk_size){

    // Computes every other Integer element of data array, starting from the second, up to the middle
    def task1(data: Array[Int]): ForkJoinTask[Unit] = task {
      ???
    }

    // Computes every other Integer element of data array, starting from the first (index 0), up to the middle
    def task2(data: Array[Int]): ForkJoinTask[Unit] = task {
      ???
    }

    // Computes every other Integer element of data array, starting from the last, up to the middle
    def task3(data: Array[Int]): ForkJoinTask[Unit] = task {
      ???
    }

    // Computes every other Integer element of data array, starting from the second to last, up to the middle
    // This is executed on the current thread.
    def task4(data: Array[Int]): Unit = {
      ???
    }

    def result(): Array[Int] = {
      val data = new Array[Int](cnt)

      ???

      data
    }

    private def copyForward(data: Array[Int], curr: Node, from: Int, to: Int, limit: Int) = {
      var current = curr
      var i_from = from
      var i_to = to 

      while (i_to < limit) {
        try {
          data(i_to) = current.value(i_from)
          i_to += 2
          i_from += 2
          if(i_from == current.cnt){
            current = current.next
            i_from = 0
          }
          else if(i_from > current.cnt) {
            current = current.next
            i_from = 1
            if(current.cnt == 1) {
              current = current.next
              i_from = 0
            }
          }
        }
        catch{
          case e: Exception =>
        }
      }
    }
  }

}
