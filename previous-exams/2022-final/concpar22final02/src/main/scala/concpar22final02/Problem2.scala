package concpar22final02

import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable.ArrayBuffer

class Problem2(imageSize: Int, numThreads: Int, numFilters: Int):

  val barrier: ArrayBuffer[Barrier] = ArrayBuffer.fill(numFilters)(Barrier(numThreads))

  val imageLib: ImageLib = ImageLib(imageSize)

  def imagePipeline(filters: Array[imageLib.Filter], row: Array[Int]): ArrayBuffer[ArrayBuffer[Int]] = ???
