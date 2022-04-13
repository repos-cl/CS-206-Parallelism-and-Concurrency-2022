# Sorted Doubly Linked-List 

The goal of this assignment is to implement a concurrent list that stores values of type `Int` in ascending order. Last week, we have seen lock-free list which uses Node marking to marks nodes to be deleted. The week, the list is a doubly linked list without any node marking. The data structure allows access to a single writer(Insert/Delete) but multiple readers can run concurrently. 

The goal will be to have a data structure that can safely allow readers to do the traversal even if writer has deleted the node. Hazard pointers will be used to achieve that.

Use the following commands to make a fresh clone of your repository:

```
git clone -b hazardptr git@gitlab.epfl.ch:lamp/student-repositories-s22/cs206-GASPAR.git cs206-hazardptr
```

## Hazard Pointer (https://ieeexplore.ieee.org/document/1291819)

Hazard Pointer is a garbage collection technique which allows concurrent access to an object in a thread even when it might be deleted by other threads. For example, thread A might be deleting a node and at the same time another thread B can have access to that same node. If thread A frees the memory, then thread B will crash because it is trying to access invalid address. 

The sorted list methods you will implement will make use of hazard pointers. Hazard pointers are a per-thread pointers which are used to protect readers in case there is a concurrent delete of the same node. For the sorted list data structure, you will use two hazard pointers per thread. 

Whenever there is a delete operation, the node is added to the per-thread retireList. If the size of the retireList exceeds some threshold, then that thread will iterate over all per-thread hazard pointers to figure out which nodes are currently not in use and can be safely deleted. 

Following is the implementation of the AbstractHazardPointer class.

```scala
abstract class AbstractHazardPointer(numPointerPerThread: Int) extends Monitor:

  val maxThreads = 10

  val threshold = 2

  val hazardPointerArray: ArrayBuffer[Option[Node]] = ArrayBuffer.fill(numPointerPerThread * maxThreads)(None)

  val retiredListArray: ArrayBuffer[ArrayBuffer[Option[Node]]] = ArrayBuffer.fill(maxThreads)(ArrayBuffer())

  // This function will be overridden when running in parallel.
  def getMyId(): Int = 0

  // Update ith per-thread hazard pointer ( 0 <= i <= numPointerPerThread )
  def update(i: Int, n: Option[Node]): Unit

  // Return ith per-thread hazard pointer ( 0 <= i <= numPointerPerThread )
  def get(i: Int): Option[Node]

  def retireNode(node: Option[Node]) : Unit
```

hazardPointerArray is an Array of hazard pointers. This array will store per-thread hazard pointers. You can use `get` / `update` functions to read/write per-thread hazard pointers.

You have to implement the retireNode function in the hazardPointer class.

```scala
class HazardPointer(numPointerPerThread: Int) extends AbstractHazardPointer(numPointerPerThread):

  def retireNode(node: Option[Node]) = ???
```
### `retireNode`

Implement the function to retire the node. The method should do the following :
- Add the node to thread's retired list.
- If the size of retired list is greater than a pre-defined threshold. Remove all the elements from the list which are not pointed by any hazard pointer of all the threads.

## The Sorted List Data Structure

The data structure you will implement is a mutable linked list of integers, sorted in ascending order. Each integer is stored in a `Node`. In addition to the integer value, each node contains a mutable reference to the next and previous node in the list.

```scala
class Node(val value: Int, var next: Option[Node] = null, var prev: Option[Node] = null) {
}
```

Then, `SortedList` is simply a class that holds a reference to the first node of the list. This class is defined in `SortedList.scala`.

```scala
class SortedList extends AbstractSortedList:
  

  // The sentinel node at the head.
  val _head: Option[Node] = Option(createNode(0, None, None, isHead = true))

  // The first logical node is referenced by the head.
  def firstNode: Option[Node] = _head.get.next

  val hazardPointers = new HazardPointer(2)

  // Finds the first node whose value satisfies the predicate.
  // Returns the predecessor of the node and the node.
  def findNodeWithPrev(pred: Int => Boolean): (Option[Node], Option[Node]) = ???

  //Find Nth Node after current element. Return 0 if out of bounds.
  def getNthNext(e: Int, n: Int): Int = ???

  // Count occurrence of the element.
  def countOccurrences(e: Int): Int = ???

  // Insert an element in the list.
  def insert(e: Int): Unit = synchronized {
    ???
    }

  // Checks if the list contains an element.
  def contains(e: Int): Boolean = ???

  // Delete an element from the list.
  // Should only delete one element when multiple occurrences are present.
  def delete(e: Int): Boolean = synchronized {
    ???
    }


```

The value `_head` of the list is called a [*sentinel* node](https://en.wikipedia.org/wiki/Sentinel_node). This value is simply `Node` that will serve only as a reference to the first actual node of the list. The value (viz. zero) held by this special node should be completely ignored. 

The data structure support single writer and multiple readers. Insert and Delete function acts as writing to the data structure and these should be executed one at a time, even though multiple threads are trying to insert/delete. Rest of the functions are reading the data structure and can be executed concurrently. Use hazard pointers in these function to protect against concurrent delete.

### `findNodeWithPrev`

The first method you will implement is an internal helper method which will be used all other methods.
This method should do a traversal of the list to find the first node whose value satisfies the parameter predicate.
The method should return the following two values as a pair:
- the predecessor of the node,
- the node.

If `node` is the first node whose value satisfies the predicate and `predecessor` its predecessor, then the method should return `(predecessor, Some(node))`. Due to the use of the sentinel node at the head of the list, the method is bound to find a predecessor, even for the first logical node. There can also be concurrent insert/delete while traversal, so the method should also check if the next of the `predecessor` node points to `node` or not. If yes, then the method should return `(predecessor, Some(node))` or else start from the beginning.

When the predicate doesn't hold on any of the values, then the function should return `(last, None)`, where
`last` is the last node of the list.

Since the traveral is lock-free, the previous and current node should be protected by hazard pointers.

### `insert` / `delete`

Implement insert and delete into doubly linked list. You can use `findNodeWithPrev` function.

### `contains`

Next, you should implement the `contains` method, which checks if the list contains a given element. 

### `getNthNext`

Implement a method which will find the first occurence of `e` and then return the value of the node which is Nth next from that node. For example, if we have a list 1->2->3->4->5, element to search(e) = 3 and N=2. Then Nth next is 5.

### `countOccurrences`

Finally, implement a method which count occurences of the element in the list.

You are now done with this assignment. Congratulations!

