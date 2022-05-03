package midterm

// Question 8

// Run with `sbt "runMain midterm.part3"`

@main def part3() =
  def thread(b: => Unit) =
    val t = new Thread:
      override def run() = b
    t
  val t = thread { println(s"Hello World") }
  t.join()
  println(s"Hello")
