package concpar22final01

trait Problem1 extends Lib:

  class DLLCombinerImplementation extends DLLCombiner:

    // Copies every other Integer element of data array, starting from the first (index 0), up to the middle
    def task1(data: Array[Int]) = task {

      var current = first
      var i = 0
      while current != null && i < size / 2 do
        data(i) = current.value
        i += 2
        current = current.getNext2
    }

    // Copies every other Integer element of data array, starting from the second, up to the middle
    def task2(data: Array[Int]) = task {

      var current = second
      var i = 1
      while current != null && i < size / 2 do
        data(i) = current.value
        i += 2
        current = current.getNext2
    }

    // Copies every other Integer element of data array, starting from the second to last, up to the middle
    def task3(data: Array[Int]) = task {

      var current = secondToLast
      var i = size - 2
      while current != null && i >= size / 2 do
        data(i) = current.value
        i -= 2
        current = current.getPrevious2
    }

    // Copies every other Integer element of data array, starting from the last, up to the middle
    // This is executed on the current thread.
    def task4(data: Array[Int]) =

      var current = last
      var i = size - 1
      while current != null && i >= size / 2 do
        data(i) = current.value
        i -= 2
        current = current.getPrevious2

    def result(): Array[Int] =
      val data = new Array[Int](size)

      val t1 = task1(data)
      val t2 = task2(data)
      val t3 = task3(data)

      task4(data)

      t1.join()
      t2.join()
      t3.join()


      data
