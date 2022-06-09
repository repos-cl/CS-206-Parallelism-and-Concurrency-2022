package concpar22final02

import scala.concurrent.*
import scala.concurrent.duration.*
import scala.collection.mutable.HashMap
import scala.util.Random
import instrumentation.SchedulableProblem2

import instrumentation.TestHelper.*
import instrumentation.TestUtils.*
import scala.collection.mutable.ArrayBuffer

class Problem2Suite extends munit.FunSuite:

  val imageSize = 5
  val nThreads = 3

  def rowsForThread(threadNumber: Int): Array[Int] =
    val start: Int = (imageSize * threadNumber) / nThreads
    val end: Int = (imageSize * (threadNumber + 1)) / nThreads
    (start until end).toArray

  test("Should work when barrier is called by a single thread (10pts)") {
    testManySchedules(
      1,
      sched =>
        val temp = new Problem2(imageSize, 1, 1)
        (
          List(() => temp.barrier(0).countDown()),
          results =>
            if sched.notifyCount == 0 && sched.notifyAllCount == 0 then
              val notifyCount = sched.notifyCount
              val notifyAllCount = sched.notifyAllCount
              (false, s"No notify call $notifyCount $notifyAllCount")
            else if temp.barrier(0).count != 0 then
              val count = temp.barrier(0).count
              (false, s"Barrier count not equal to zero: $count")
            else (true, "")
        )
    )
  }

  test("Should work when a single thread processes a single filter (10pts)") {
    val temp = new Problem2(imageSize, 1, 1)
    val buf: ArrayBuffer[ArrayBuffer[Int]] = new ArrayBuffer()
    for i: Int <- 0 until imageSize do buf += ArrayBuffer.fill(5)(i)
    temp.imageLib.init(buf)
    temp.imagePipeline(Array(temp.imageLib.Filter.Outline), Array(0, 1, 2, 3, 4))
    assertEquals(
      temp.imageLib.buffer1,
      ArrayBuffer(
        ArrayBuffer(0, 0, 0, 0, 0),
        ArrayBuffer(1, 1, 1, 1, 1),
        ArrayBuffer(2, 2, 2, 2, 2),
        ArrayBuffer(3, 3, 3, 3, 3),
        ArrayBuffer(4, 4, 4, 4, 4)
      )
    )
    assertEquals(
      temp.imageLib.buffer2,
      ArrayBuffer(
        ArrayBuffer(-2, -3, -3, -3, -2),
        ArrayBuffer(3, 0, 0, 0, 3),
        ArrayBuffer(6, 0, 0, 0, 6),
        ArrayBuffer(9, 0, 0, 0, 9),
        ArrayBuffer(22, 15, 15, 15, 22)
      )
    )
  }

  test("Should work when a single thread processes a 2 same filters (15pts)") {
    val temp = new Problem2(imageSize, 1, 2)
    val buf: ArrayBuffer[ArrayBuffer[Int]] = new ArrayBuffer()
    for i: Int <- 0 until imageSize do buf += ArrayBuffer.fill(5)(i)
    temp.imageLib.init(buf)
    temp.imagePipeline(
      Array(temp.imageLib.Filter.Identity, temp.imageLib.Filter.Identity),
      Array(0, 1, 2, 3, 4)
    )
    assertEquals(
      temp.imageLib.buffer1,
      ArrayBuffer(
        ArrayBuffer(0, 0, 0, 0, 0),
        ArrayBuffer(1, 1, 1, 1, 1),
        ArrayBuffer(2, 2, 2, 2, 2),
        ArrayBuffer(3, 3, 3, 3, 3),
        ArrayBuffer(4, 4, 4, 4, 4)
      )
    )
    assertEquals(
      temp.imageLib.buffer2,
      ArrayBuffer(
        ArrayBuffer(0, 0, 0, 0, 0),
        ArrayBuffer(1, 1, 1, 1, 1),
        ArrayBuffer(2, 2, 2, 2, 2),
        ArrayBuffer(3, 3, 3, 3, 3),
        ArrayBuffer(4, 4, 4, 4, 4)
      )
    )
  }

  test("Should work when a single thread processes a 2 different filters (15pts)") {
    val temp = new Problem2(imageSize, 1, 2)
    val buf: ArrayBuffer[ArrayBuffer[Int]] = new ArrayBuffer()
    for i: Int <- 0 until imageSize do buf += ArrayBuffer.fill(5)(i)
    temp.imageLib.init(buf)
    temp.imagePipeline(
      Array(temp.imageLib.Filter.Identity, temp.imageLib.Filter.Outline),
      Array(0, 1, 2, 3, 4)
    )
    assertEquals(
      temp.imageLib.buffer1,
      ArrayBuffer(
        ArrayBuffer(-2, -3, -3, -3, -2),
        ArrayBuffer(3, 0, 0, 0, 3),
        ArrayBuffer(6, 0, 0, 0, 6),
        ArrayBuffer(9, 0, 0, 0, 9),
        ArrayBuffer(22, 15, 15, 15, 22)
      )
    )
    assertEquals(
      temp.imageLib.buffer2,
      ArrayBuffer(
        ArrayBuffer(0, 0, 0, 0, 0),
        ArrayBuffer(1, 1, 1, 1, 1),
        ArrayBuffer(2, 2, 2, 2, 2),
        ArrayBuffer(3, 3, 3, 3, 3),
        ArrayBuffer(4, 4, 4, 4, 4)
      )
    )
  }

  test("Should work when barrier is called by two threads (25pts)") {
    testManySchedules(
      2,
      sched =>
        val temp = new Problem2(imageSize, 2, 1)
        (
          List(
            () =>
              temp.barrier(0).countDown()
              temp.barrier(0).awaitZero()
            ,
            () =>
              temp.barrier(0).countDown()
              temp.barrier(0).awaitZero()
          ),
          results =>
            if sched.notifyCount == 0 && sched.notifyAllCount == 0 then (false, s"No notify call")
            else if sched.waitCount == 0 then (false, s"No wait call")
            else if temp.barrier(0).count != 0 then
              val count = temp.barrier(0).count
              (false, s"Barrier count not equal to zero: $count")
            else (true, "")
        )
    )
  }

  test("Should work when barrier is called by multiple threads (25pts)") {
    testManySchedules(
      nThreads,
      sched =>
        val temp = new Problem2(imageSize, nThreads, 1)
        (
          (for i <- 0 until nThreads yield () =>
            temp.barrier(0).countDown()
            temp.barrier(0).awaitZero()
          ).toList,
          results =>
            if sched.notifyCount == 0 && sched.notifyAllCount == 0 then (false, s"No notify call")
            else if sched.waitCount == 0 then (false, s"No wait call")
            else if temp.barrier(0).count != 0 then
              val count = temp.barrier(0).count
              (false, s"Barrier count not equal to zero: $count")
            else (true, "")
        )
    )
  }

  test("Should work when a single thread processes a multiple same filters (25pts)") {
    val temp = new Problem2(imageSize, 1, 3)
    val buf: ArrayBuffer[ArrayBuffer[Int]] = new ArrayBuffer()
    for i: Int <- 0 until imageSize do buf += ArrayBuffer.fill(5)(i)
    temp.imageLib.init(buf)
    temp.imagePipeline(
      Array(
        temp.imageLib.Filter.Outline,
        temp.imageLib.Filter.Outline,
        temp.imageLib.Filter.Outline
      ),
      Array(0, 1, 2, 3, 4)
    )
    assertEquals(
      temp.imageLib.buffer2,
      ArrayBuffer(
        ArrayBuffer(-128, -173, -107, -173, -128),
        ArrayBuffer(205, -2, 172, -2, 205),
        ArrayBuffer(322, -128, 208, -128, 322),
        ArrayBuffer(55, -854, -428, -854, 55),
        ArrayBuffer(1180, 433, 751, 433, 1180)
      )
    )
    assertEquals(
      temp.imageLib.buffer1,
      ArrayBuffer(
        ArrayBuffer(-16, -22, -18, -22, -16),
        ArrayBuffer(23, -1, 9, -1, 23),
        ArrayBuffer(36, -18, 0, -18, 36),
        ArrayBuffer(29, -67, -45, -67, 29),
        ArrayBuffer(152, 74, 90, 74, 152)
      )
    )
  }

  test("Should work when a single thread processes multiple filters (25pts)") {
    val temp = new Problem2(imageSize, 1, 3)
    val buf: ArrayBuffer[ArrayBuffer[Int]] = new ArrayBuffer()
    for i: Int <- 0 until imageSize do buf += ArrayBuffer.fill(5)(i)
    temp.imageLib.init(buf)
    temp.imagePipeline(
      Array(
        temp.imageLib.Filter.Identity,
        temp.imageLib.Filter.Outline,
        temp.imageLib.Filter.Sharpen
      ),
      Array(0, 1, 2, 3, 4)
    )
    assertEquals(
      temp.imageLib.buffer1,
      ArrayBuffer(
        ArrayBuffer(-2, -3, -3, -3, -2),
        ArrayBuffer(3, 0, 0, 0, 3),
        ArrayBuffer(6, 0, 0, 0, 6),
        ArrayBuffer(9, 0, 0, 0, 9),
        ArrayBuffer(22, 15, 15, 15, 22)
      )
    )
    assertEquals(
      temp.imageLib.buffer2,
      ArrayBuffer(
        ArrayBuffer(-10, -10, -9, -10, -10),
        ArrayBuffer(11, 0, 3, 0, 11),
        ArrayBuffer(18, -6, 0, -6, 18),
        ArrayBuffer(17, -24, -15, -24, 17),
        ArrayBuffer(86, 38, 45, 38, 86)
      )
    )
  }

  test("Should work when multiple thread processes a single filter (25pts)") {
    testManySchedules(
      nThreads,
      sched =>
        val temp = new SchedulableProblem2(sched, imageSize, nThreads, 1)
        (
          (for i <- 0 until nThreads
          yield () =>
            temp.imagePipeline(Array(temp.imageLib.Filter.Outline), rowsForThread(i))).toList,
          results =>
            val expected_buffer1 = ArrayBuffer(
              ArrayBuffer(1, 1, 1, 1, 1),
              ArrayBuffer(1, 1, 1, 1, 1),
              ArrayBuffer(1, 1, 1, 1, 1),
              ArrayBuffer(1, 1, 1, 1, 1),
              ArrayBuffer(1, 1, 1, 1, 1)
            )
            val expected_buffer2 = ArrayBuffer(
              ArrayBuffer(5, 3, 3, 3, 5),
              ArrayBuffer(3, 0, 0, 0, 3),
              ArrayBuffer(3, 0, 0, 0, 3),
              ArrayBuffer(3, 0, 0, 0, 3),
              ArrayBuffer(5, 3, 3, 3, 5)
            )
            val res_buffer1 = temp.imageLib.buffer1
            val res_buffer2 = temp.imageLib.buffer2
            if res_buffer1 != expected_buffer1 then
              (false, s"Buffer1 expected: $expected_buffer1 , got $res_buffer1")
            else if res_buffer2 != expected_buffer2 then
              (false, s"Buffer2 expected: $expected_buffer2 , got $res_buffer2")
            else (true, "")
        )
    )
  }

  test("Should work when multiple thread processes two filters (25pts)") {
    testManySchedules(
      nThreads,
      sched =>
        val temp = new SchedulableProblem2(sched, imageSize, nThreads, 2)
        (
          (for i <- 0 until nThreads
          yield () =>
            temp.imagePipeline(
              Array(temp.imageLib.Filter.Outline, temp.imageLib.Filter.Sharpen),
              rowsForThread(i)
            )).toList,
          results =>
            val expected_buffer1 = ArrayBuffer(
              ArrayBuffer(19, 7, 9, 7, 19),
              ArrayBuffer(7, -6, -3, -6, 7),
              ArrayBuffer(9, -3, 0, -3, 9),
              ArrayBuffer(7, -6, -3, -6, 7),
              ArrayBuffer(19, 7, 9, 7, 19)
            )
            val expected_buffer2 = ArrayBuffer(
              ArrayBuffer(5, 3, 3, 3, 5),
              ArrayBuffer(3, 0, 0, 0, 3),
              ArrayBuffer(3, 0, 0, 0, 3),
              ArrayBuffer(3, 0, 0, 0, 3),
              ArrayBuffer(5, 3, 3, 3, 5)
            )
            val res_buffer1 = temp.imageLib.buffer1
            val res_buffer2 = temp.imageLib.buffer2
            if res_buffer1 != expected_buffer1 then
              (false, s"Buffer1 expected: $expected_buffer1 , got $res_buffer1")
            else if res_buffer2 != expected_buffer2 then
              (false, s"Buffer2 expected: $expected_buffer2 , got $res_buffer2")
            else (true, "")
        )
    )
  }

  test("Should work when multiple thread processes multiple same filters (25pts)") {
    testManySchedules(
      nThreads,
      sched =>
        val temp = new SchedulableProblem2(sched, imageSize, nThreads, 4)
        val buf: ArrayBuffer[ArrayBuffer[Int]] = new ArrayBuffer()
        for i: Int <- 0 until imageSize do buf += ArrayBuffer.fill(5)(i)
        temp.imageLib.init(buf)
        (
          (for i <- 0 until nThreads
          yield () =>
            temp.imagePipeline(
              Array(
                temp.imageLib.Filter.Identity,
                temp.imageLib.Filter.Identity,
                temp.imageLib.Filter.Identity,
                temp.imageLib.Filter.Identity
              ),
              rowsForThread(i)
            )).toList,
          results =>
            val expected_buffer1 = ArrayBuffer(
              ArrayBuffer(0, 0, 0, 0, 0),
              ArrayBuffer(1, 1, 1, 1, 1),
              ArrayBuffer(2, 2, 2, 2, 2),
              ArrayBuffer(3, 3, 3, 3, 3),
              ArrayBuffer(4, 4, 4, 4, 4)
            )
            val expected_buffer2 = ArrayBuffer(
              ArrayBuffer(0, 0, 0, 0, 0),
              ArrayBuffer(1, 1, 1, 1, 1),
              ArrayBuffer(2, 2, 2, 2, 2),
              ArrayBuffer(3, 3, 3, 3, 3),
              ArrayBuffer(4, 4, 4, 4, 4)
            )
            val res_buffer1 = temp.imageLib.buffer1
            val res_buffer2 = temp.imageLib.buffer2
            if res_buffer1 != expected_buffer1 then
              (false, s"Buffer1 expected: $expected_buffer1 , got $res_buffer1")
            else if res_buffer2 != expected_buffer2 then
              (false, s"Buffer2 expected: $expected_buffer2 , got $res_buffer2")
            else (true, "")
        )
    )
  }

  test("Should work when multiple thread processes multiple different filters (25pts)") {
    testManySchedules(
      nThreads,
      sched =>
        val temp = new SchedulableProblem2(sched, imageSize, nThreads, 4)
        val buf: ArrayBuffer[ArrayBuffer[Int]] = new ArrayBuffer()
        for i: Int <- 0 until imageSize do buf += ArrayBuffer.fill(5)(i)
        temp.imageLib.init(buf)
        (
          (for i <- 0 until nThreads
          yield () =>
            temp.imagePipeline(
              Array(
                temp.imageLib.Filter.Outline,
                temp.imageLib.Filter.Sharpen,
                temp.imageLib.Filter.Identity,
                temp.imageLib.Filter.Sharpen
              ),
              rowsForThread(i)
            )).toList,
          results =>
            val expected_buffer1 = ArrayBuffer(
              ArrayBuffer(-51, -31, -28, -31, -51),
              ArrayBuffer(47, 2, 24, 2, 47),
              ArrayBuffer(68, -24, 24, -24, 68),
              ArrayBuffer(5, -154, -72, -154, 5),
              ArrayBuffer(375, 83, 164, 83, 375)
            )
            val expected_buffer2 = ArrayBuffer(
              ArrayBuffer(-10, -10, -9, -10, -10),
              ArrayBuffer(11, 0, 3, 0, 11),
              ArrayBuffer(18, -6, 0, -6, 18),
              ArrayBuffer(17, -24, -15, -24, 17),
              ArrayBuffer(86, 38, 45, 38, 86)
            )
            val res_buffer1 = temp.imageLib.buffer1
            val res_buffer2 = temp.imageLib.buffer2
            if res_buffer1 != expected_buffer1 then
              (false, s"Buffer1 expected: $expected_buffer1 , got $res_buffer1")
            else if res_buffer2 != expected_buffer2 then
              (false, s"Buffer2 expected: $expected_buffer2 , got $res_buffer2")
            else (true, "")
        )
    )
  }
