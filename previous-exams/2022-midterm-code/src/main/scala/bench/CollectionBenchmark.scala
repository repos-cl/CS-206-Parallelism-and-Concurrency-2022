package bench

import org.openjdk.jmh.annotations.*

class CollectionBenchmark extends AbstractCollectionBenchmark:
  @Benchmark
  def take() =
    haystack.take(size / 2)

  @Benchmark
  def drop() =
    haystack.drop(size / 2)
