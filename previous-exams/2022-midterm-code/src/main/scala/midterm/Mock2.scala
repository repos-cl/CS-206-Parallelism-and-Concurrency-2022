package midterm

import midterm.instrumentation.Monitor

class Account(private var amount: Int = 0) extends Monitor:
  def transfer(target: Account, n: Int) =
    this.synchronized {
      target.synchronized {
        this.amount -= n
        target.amount += n
      }
    }

@main def mock2() =
  val a = new Account(50)
  val b = new Account(70)
  val t1 = task { a.transfer(b, 10) }
  val t2 = task { b.transfer(a, 10) }
  t1.join()
  t2.join()
