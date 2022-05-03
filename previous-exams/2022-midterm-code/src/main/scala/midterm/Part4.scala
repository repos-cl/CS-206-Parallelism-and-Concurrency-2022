package midterm

import java.util.concurrent.atomic.AtomicInteger
import instrumentation.*

// Questions 9-15

// See tests in midterm.Part4Test.
// Run with `sbt testOnly midterm.Part4Test`.

class Node(
    // Globally unique identifier. Different for each instance.
    val guid: Int
) extends Monitor

// This function might be called concurrently.
def lockFun(nodes: List[Node], fn: (e: Int) => Unit): Unit =
  if nodes.size > 0 then
    nodes.head.synchronized {
      fn(nodes(0).guid)
      lockFun(nodes.tail, fn)
    }
