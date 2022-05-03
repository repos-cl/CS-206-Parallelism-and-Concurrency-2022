package bench

import midterm.contains

import org.openjdk.jmh.annotations.*

class MidtermPart2Benchmark extends AbstractCollectionBenchmark:
  val needle = 10

  @Param(Array("true", "false"))
  var parallel: Boolean = _

  @Setup(Level.Invocation)
  override def setup() =
    super.setup()
    midterm.parallelismEnabled = parallel

  @Benchmark
  def containsBenchmark() =
    contains(haystack, needle)
