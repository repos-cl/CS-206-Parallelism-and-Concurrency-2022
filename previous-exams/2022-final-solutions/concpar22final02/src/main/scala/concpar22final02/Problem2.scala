package concpar22final02

import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable.ArrayBuffer

class Problem2(imageSize: Int, numThreads: Int, numFilters: Int):

  val barrier: ArrayBuffer[Barrier] = ArrayBuffer.fill(numFilters)(Barrier(numThreads))

  val imageLib: ImageLib = ImageLib(imageSize)


  def imagePipeline(
      filters: Array[imageLib.Filter],
      rows: Array[Int]
  ): ArrayBuffer[ArrayBuffer[Int]] =
    for i <- 0 to filters.size - 1 do
      for j <- 0 to rows.size - 1 do
        if i % 2 == 0 then
          imageLib.applyFilter(filters(i).kernel, imageLib.buffer1, imageLib.buffer2, rows(j))
        else
          imageLib.applyFilter(filters(i).kernel, imageLib.buffer2, imageLib.buffer1, rows(j))

      barrier(i).countDown()
      barrier(i).awaitZero()

    if filters.size % 2 == 0 then imageLib.buffer1
    else imageLib.buffer2

