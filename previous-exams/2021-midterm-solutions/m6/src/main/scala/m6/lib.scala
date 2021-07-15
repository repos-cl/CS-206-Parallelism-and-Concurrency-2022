package m6

import java.util.concurrent._
import scala.util.DynamicVariable

trait Lib {

  class Node(val size: Int) {
    var value: Array[Int] = new Array(size)
    var next: Node = null    
    var previous: Node = null
    var cnt = 0

    def add(v: Int) = {
      value(cnt) = v
      cnt += 1
    }
  }

  // Simplified Combiner interface
  // Implements methods += and combine
  // Abstract methods should be implemented in subclasses
  abstract class DLLCombiner(val chunk_size: Int) {
    var head: Node = null
    var last: Node = null
    var cnt: Int = 0
    var chunks: Int = 0

    // Adds an Integer to the last node of this array combiner. If the last node is full, allocates a new node.
    def +=(elem: Int): Unit = {
      if(cnt % chunk_size == 0) {
        chunks = chunks + 1
        val node = new Node(chunk_size)
        if (cnt == 0) {
          head = node
          last = node
        }
        else {
          last.next = node
          node.previous = last
          last = node
        }
      }
      last.add(elem)
      cnt += 1
    }

    // Combines this array combiner and another given combiner in constant O(1) complexity.
    def combine(that: DLLCombiner): DLLCombiner = {
      assert(this.chunk_size == that.chunk_size)
      if (this.cnt == 0) {
        this.head = that.head
        this.last = that.last
        this.cnt = that.cnt
        this.chunks = that.chunks

        this
      }
      else if (that.cnt == 0)
        this
      else {
        this.last.next = that.head
        that.head.previous = this.last

        this.cnt = this.cnt + that.cnt
        this.chunks = this.chunks + that.chunks
        this.last = that.last

        this
      }
    }

    def task1(data: Array[Int]): ForkJoinTask[Unit]
    def task2(data: Array[Int]): ForkJoinTask[Unit]
    def task3(data: Array[Int]): ForkJoinTask[Unit]
    def task4(data: Array[Int]): Unit

    def result(): Array[Int]

  }

  def task[T](body: => T): ForkJoinTask[T]

}