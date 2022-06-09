package concpar22final01

trait Problem1 extends Lib:

  class DLLCombinerImplementation extends DLLCombiner:

    // Copies every other Integer element of data array, starting from the first (index 0), up to the middle
    def task1(data: Array[Int]) = task {
      ???
    }

    // Copies every other Integer element of data array, starting from the second, up to the middle
    def task2(data: Array[Int]) = task {
      ???
    }

    // Copies every other Integer element of data array, starting from the second to last, up to the middle
    def task3(data: Array[Int]) = task {
      ???
    }

    // Copies every other Integer element of data array, starting from the last, up to the middle
    // This is executed on the current thread.
    def task4(data: Array[Int]) =
      ???

    def result(): Array[Int] =
      val data = new Array[Int](size)
      ???

      data
