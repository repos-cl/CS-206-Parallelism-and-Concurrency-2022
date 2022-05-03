package bench

import org.openjdk.jmh.annotations.*

@State(Scope.Benchmark)
//@Fork(jvmArgsAppend = Array("-Djava.util.concurrent.ForkJoinPool.common.parallelism=4"))
abstract class AbstractCollectionBenchmark:
  @Param(Array("10000", "100000", "1000000", "10000000"))
  var size: Int = _

  @Param(Array("Vector", "Array", "ArrayBuffer", "List"))
  var collection: String = _

  var haystack: Iterable[Int] = _

  @Setup(Level.Invocation)
  def setup() =
    val gen = (1 to (size * 2) by 2)
    haystack = collection match
      case "Vector"      => gen.toVector
      case "Array"       => gen.toArray
      case "ArrayBuffer" => gen.toBuffer
      case "List"        => gen.toList
