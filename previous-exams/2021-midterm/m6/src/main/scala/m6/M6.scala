package m6

import java.util.concurrent._
import scala.util.DynamicVariable

trait M6 extends Lib {

  class DLLCombinerImplementation(chunk_size: Int = 3) extends DLLCombiner(chunk_size){

    // Computes every other Integer element of data array, starting from the first (index 0), up to the middle
    def task1(data: Array[Int]): ForkJoinTask[Unit] = task {

      var current = head
      if(current != null) {
        var i_from = 0
        var i_to = 0
        copyForward(data, current, i_from, i_to, cnt/2)
      }
    }

    // Computes every other Integer element of data array, starting from the second, up to the middle
    def task2(data: Array[Int]): ForkJoinTask[Unit] = task {

      var current = head
      if(current != null) {
        var i_from = 1
        var i_to = 1

        if(i_from >= current.cnt) {
          current = current.next
          if(current != null) {
            i_from = 0
          }
          else i_to = cnt/2 // to stop the loop
        }

        copyForward(data, current, i_from, i_to, cnt/2)
      }  
    }

    // Computes every other Integer element of data array, starting from the second to last, up to the middle
    def task3(data: Array[Int]): ForkJoinTask[Unit] = task {

      var current = last
      if( current != null) {
        var i_to = cnt - 2
        var i_from = current.cnt - 2
        if(i_from < 0) {
          current = current.previous
          if(current != null) {
            i_from = current.cnt - 1
          }
          else i_to = cnt/2 - 1 // to stop the loop
        }

        copyBackward(data, current, i_from, i_to, cnt/2)
      }
    }

    // Computes every other Integer element of data array, starting from the last, up to the middle
    // This is executed on the current thread.
    def task4(data: Array[Int]): Unit = {

      var current = last
      if( current != null) {
        var i_from = current.cnt - 1
        var i_to = cnt - 1
        copyBackward(data, current, i_from, i_to, cnt/2)
      }
    }

    def result(): Array[Int] = {
      val data = new Array[Int](cnt)

      val t1 = task1(data)
      val t2 = task2(data)
      val t3 = task3(data)

      task4(data)

      t1.join()
      t2.join()
      t3.join()


      data
    }


    private def copyBackward(data: Array[Int], curr: Node, from: Int, to: Int, limit: Int) = {
      var current = curr
      var i_from = from
      var i_to = to 

      while (i_to >= limit) {
        try{
          data(i_to) = current.value(i_from)
          i_to -= 2
          i_from -= 2
          if(i_from == -1) {
            current = current.previous
            i_from = current.cnt - 1
          }
          else if(i_from == -2) {
            current = current.previous
            i_from = current.cnt - 2
            if(current.cnt == 1) {
              current = current.previous
              i_from = current.cnt - 1
            }
          }
        }
        catch{
          case e: Exception =>
        }
      }
        
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
