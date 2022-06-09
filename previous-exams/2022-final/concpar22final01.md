# Problem 1: Combiners

## Setup

Use the following commands to make a fresh clone of your repository:

```
git clone -b concpar22final01 git@gitlab.epfl.ch:lamp/student-repositories-s22/cs206-GASPAR.git concpar22final01
```

If you have issues with the IDE, try [reimporting the
build](https://gitlab.epfl.ch/lamp/cs206/-/blob/master/labs/example-lab.md#troubleshooting),
if you still have problems, use `compile` in sbt instead.

## Exercise

In this exercise, you will implement an array Combiner. The Combiner internally uses a double linked list whose nodes also point to their successor's successor and their predecessor's predecessor. Your goal is to complete the implementation of the (simplified) Combiner interface, by implementing the `result` method to compute the result array from this array combiner.

Here you can see the declaration of the `DLLCombiner` class and the related `Node` class definition. Look at the `Lib` trait in the `lib.scala` file to find all definitions of relevant functions and classes.

```scala
  class Node(val value: Int):
    protected var next: Node = null      // null for last node.
    protected var next2: Node = null     // null for last node.
    protected var previous: Node = null  // null for first node.
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
  // Implements methods `+=` and `combine`
  // Abstract methods should be implemented in subclasses
  abstract class DLLCombiner
```

`DLLCombiner` class contains the implementation of methods `+=` and `combine`. You should look at them to better understand the structure of this array Combiner, before moving on to solving this exercise. 

Your task in the exercise will be to implement the `result` method of the `DLLCombinerImplementation` class. This method should compute the result array from this array combiner. In your solution, you should **not** use methods `getNext` and `getPrevious`, but only `getNext2` and `getPrevious2`, to reduce the number of moving operations.

According to the Combiner contract, `result` should work in parallel. Implement this method efficiently using 4 parallel tasks, by copying the double linked list to the array from both ends at the same time. Two threads should start from the start of the list and two from the end. In each case, one thread would be responsible for odd indexes and the other for even ones.

Following the description above, your task in the exercise is to:

 1. Implement the four tasks to copy parts of the resulting array. Each task is responsible for copying one quarter of the array:
   - `task1` copies every other Integer element of data array, starting from the first (index 0), up to the middle
   - `task2` copies every other Integer element of data array, starting from the second, up to the middle
   - `task3` copies every other Integer element of data array, starting from the second to last, up to the middle
   - `task4` copies every other Integer element of data array, starting from the last, up to the middle
 2. Implement the method `result` to compute the result array in parallel using those four tasks.

 Here is one example of the `result` method:

```scala
    val combiner1 = new DLLCombinerImplementation
    combiner1 += 7 
    combiner1 += 2 
    combiner1 += 4 
    combiner1 += 3 
    combiner1 += 9 
    combiner1 += 5 
    combiner1 += 1 

    val res1 = combiner1.result() // (7, 2, 4, 3, 9, 5, 1)
```
In this example, `task1` was responsible for copying elements at indexes 0 and 2, `task2` for copying the element at index 1, `task3` for copying elements at indexes 5 and 3, and `task4` for copying element at indexes 6 and 4.

Here is another example with combining:

```scala
    val c1 = new DLLCombinerImplementation
    c1 += 7 
    c1 += 2 
    c1 += 4 
    c1 += 3 
    c1 += 9 
    c1 += 5 
    c1 += 1 

    val c2 = new DLLCombinerImplementation
    c2 += 6 
    c2 += 8 
    c2 += 5 
    c2 += 1 

    val c3 = new DLLCombinerImplementation
    c3 += 1

    c1.combine(c2).combine(c3) 
    val res = c1.result() // (7, 2, 4, 3, 9, 5, 1, 6, 8, 5, 1, 1)
```

You can get partial points for solving parts of this exercise.
In your solution you should only make changes to the `DLLCombinerImplementation` class. You are not allowed to change the file `lib.scala`.
