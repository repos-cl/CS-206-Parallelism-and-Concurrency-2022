package midterm

import scala.collection.mutable.Set

@main def mock1() =
  val values = Set[Int]()
  for _ <- 1 to 100000 do
    var sum = 0
    val t1 = task { sum += 1 }
    val t2 = task { sum += 1 }
    t1.join()
    t2.join()
    values += sum
  println(values)
