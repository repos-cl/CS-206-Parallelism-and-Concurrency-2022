package concpar22final01

import java.util.concurrent.*
import scala.util.DynamicVariable

trait Lib:
  class Node(val value: Int):
    protected var next: Node = null // null for last node.
    protected var next2: Node = null // null for last node.
    protected var previous: Node = null // null for first node.
    protected var previous2: Node = null // null for first node.

    def getNext: Node = next // do NOT use in the result method
    def getNext2: Node = next2
    def getPrevious: Node = previous // do NOT use in the result method
    def getPrevious2: Node = previous2

    def setNext(n: Node): Unit = next = n
    def setNext2(n: Node): Unit = next2 = n
    def setPrevious(n: Node): Unit = previous = n
    def setPrevious2(n: Node): Unit = previous2 = n

  // Simplified Combiner interface
  // Implements methods += and combine
  // Abstract methods should be implemented in subclasses
  abstract class DLLCombiner:
    var first: Node = null // null for empty lists.
    var last: Node = null // null for empty lists.

    var second: Node = null // null for empty lists.
    var secondToLast: Node = null // null for empty lists.

    var size: Int = 0

    // Adds an Integer to this array combiner.
    def +=(elem: Int): Unit =
      val node = new Node(elem)
      if size == 0 then
        first = node
        last = node
        size = 1
      else
        last.setNext(node)
        node.setPrevious(last)
        node.setPrevious2(last.getPrevious)
        if size > 1 then last.getPrevious.setNext2(node)
        else second = node
        secondToLast = last
        last = node
        size += 1

    // Combines this array combiner and another given combiner in constant O(1) complexity.
    def combine(that: DLLCombiner): DLLCombiner =
      if this.size == 0 then that
      else if that.size == 0 then this
      else
        this.last.setNext(that.first)
        this.last.setNext2(that.first.getNext)
        if this.last.getPrevious != null then
          this.last.getPrevious.setNext2(that.first) // important

        that.first.setPrevious(this.last)
        that.first.setPrevious2(this.last.getPrevious)
        if that.first.getNext != null then that.first.getNext.setPrevious2(this.last) // important

        if this.size == 1 then second = that.first

        this.size = this.size + that.size
        this.last = that.last
        this.secondToLast = that.secondToLast

        this

    def task1(data: Array[Int]): ForkJoinTask[Unit]
    def task2(data: Array[Int]): ForkJoinTask[Unit]
    def task3(data: Array[Int]): ForkJoinTask[Unit]
    def task4(data: Array[Int]): Unit

    def result(): Array[Int]

  def task[T](body: => T): ForkJoinTask[T]
