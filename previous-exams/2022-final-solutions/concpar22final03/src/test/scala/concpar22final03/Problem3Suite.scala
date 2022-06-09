package concpar22final03

import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}
import scala.util.{Try, Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

class Problem3Suite extends munit.FunSuite:
  trait Prob3Test extends Problem3:
    override val economics: EconomicsTest
  class Test1 extends Prob3Test:
    override val economics: EconomicsTest = new EconomicsTest:
      override def sellWaitTime() = 10
      override def buyWaitTime() = 20
      override def depositWaitTime() = 30
      override def withdrawWaitTime() = 40
      override def initialBalance() = 0
  class Test2 extends Prob3Test:
    override val economics: EconomicsTest = new EconomicsTest:
      override def sellWaitTime() = 100
      override def buyWaitTime() = 5
      override def depositWaitTime() = 50
      override def withdrawWaitTime() = 5
      override def initialBalance() = 0

  class Test3 extends Prob3Test:
    override val economics: EconomicsTest = new EconomicsTest:
      val rgen = new scala.util.Random(666)
      override def sellWaitTime() = rgen.nextInt(100)
      override def buyWaitTime() = rgen.nextInt(100)
      override def depositWaitTime() = rgen.nextInt(100)
      override def withdrawWaitTime() = rgen.nextInt(100)
      override def initialBalance() = 0

  class Test4 extends Prob3Test:
    var counter = 5
    def next(): Int =
      counter = counter + 5 % 119
      counter
    override val economics: EconomicsTest = new EconomicsTest:
      override def sellWaitTime() = next()
      override def buyWaitTime() = next()
      override def depositWaitTime() = next()
      override def withdrawWaitTime() = next()
      override def initialBalance() = next()

  def testCases = List(new Test1, new Test2)
  def unevenTestCases = List(new Test3, new Test4)

  def tot(cards: List[String]): Int =
    cards.map[Int]((n: String) => n.length).sum

  def testOk(
      t: Prob3Test,
      money: Int,
      sold: List[String],
      wanted: List[String]
  ): Unit =
    import t.*
    import economics.*
    val f = orderDeck(getMoneyBag(money), sold.map(getCard), wanted)
    val r = Await.ready(f, 3.seconds).value.get
    assert(r.isSuccess)
    r match
      case Success(d) =>
        assertEquals(d.map(_.name).sorted, wanted.sorted)
        assertEquals(d.length, wanted.length)
        assertEquals(isMine(d.head), true)
      case Failure(e) => ()

  def testFailure(
      t: Prob3Test,
      money: Int,
      sold: List[String],
      wanted: List[String]
  ): Unit =
    import t.*
    import economics.*
    val f = orderDeck(getMoneyBag(money), sold.map(getCard), wanted)
    val r = Await.ready(f, 3.seconds).value.get
    assert(r.isFailure)
    r match
      case Failure(e: NotEnoughMoneyException) => ()
      case _ => fail("Should have thrown a NotEnoughMoneyException exception, but did not")

  // --- Without sold cards ---

  test(
    "Should work correctly when a single card is asked with enough money (no card sold) (20pts)"
  ) {
    testCases.foreach(t => testOk(t, 7, Nil, List("Tefeiri")))
  }
  test(
    "Should work correctly when a single card is asked with enough money (no card sold, uneven waiting time) (10pts)"
  ) {
    unevenTestCases.foreach(t => testOk(t, 7, Nil, List("Tefeiri")))
  }
  test(
    "Should work correctly when multiple cards are asked with enough money (no card sold) (20pts)"
  ) {
    val cards = List("aaaa", "bbb", "ccccc", "dd", "eeee", "f", "ggggggg")
    testCases.foreach(t => testOk(t, tot(cards), Nil, cards))
  }
  test(
    "Should work correctly when multiple cards are asked with enough money (no card sold, uneven waiting time) (10pts)"
  ) {
    val cards = List("aaaa", "bbb", "ccccc", "dd", "eeee", "f", "ggggggg")
    unevenTestCases.foreach(t => testOk(t, tot(cards), Nil, cards))
  }
  test(
    "Should work correctly when asked duplicates of cards, with enough money (no card sold) (20pts)"
  ) {
    val cards = List("aaaa", "aaaa", "aaaa", "dd", "dd", "dd", "dd")
    testCases.foreach(t => testOk(t, tot(cards), Nil, cards))
  }
  test(
    "Should work correctly when asked duplicates of cards, with enough money (no card sold, uneven waiting time) (10pts)"
  ) {
    val cards = List("aaaa", "aaaa", "aaaa", "dd", "dd", "dd", "dd")
    unevenTestCases.foreach(t => testOk(t, tot(cards), Nil, cards))
  }

  // --- With sold cards ---

  test(
    "Should work correctly when a single card is bought and a single of the same price is sold (20pts)"
  ) {
    testCases.foreach(t => testOk(t, 0, List("Chandra"), List("Tefeiri")))
  }
  test(
    "Should work correctly when a single card is bought and a single of the same price is sold (uneven waiting time) (10pts)"
  ) {
    unevenTestCases.foreach(t => testOk(t, 0, List("Chandra"), List("Tefeiri")))
  }

  test(
    "Should work correctly when multiple cards are asked and multiple of matching values are sold (20pts)"
  ) {
    val cards = List("aaaa", "bbb", "ccccc", "dd", "eeee", "f", "ggggggg")
    val sold = List("1111111", "2", "3333", "44", "55555", "666", "7777")
    testCases.foreach(t => testOk(t, 0, sold, cards))
  }
  test(
    "Should work correctly when multiple cards are asked and multiple of matching values are sold (uneven waiting time) (10pts)"
  ) {
    val cards = List("aaaa", "bbb", "ccccc", "dd", "eeee", "f", "ggggggg")
    val sold = List("1111111", "2", "3333", "44", "55555", "666", "7777")
    unevenTestCases.foreach(t => testOk(t, 0, sold, cards))
  }
  test(
    "Should work correctly when multiple cards are asked and multiple of the same total value are sold (20pts)"
  ) {
    val cards2 = List("aaaa", "bbb", "ccccc", "dd", "eeee", "f", "ggggggg")
    val sold2 = List("111111111", "22", "3", "44", "555555", "666", "777")
    assert(tot(sold2) == tot(cards2))
    testCases.foreach(t => testOk(t, 0, sold2, cards2))
  }
  test(
    "Should work correctly when multiple cards are asked and multiple of the same total value are sold (uneven waiting time) (10pts)"
  ) {
    val cards2 = List("aaaa", "bbb", "ccccc", "dd", "eeee", "f", "ggggggg")
    val sold2 = List("111111111", "22", "3", "44", "555555", "666", "777")
    assert(tot(sold2) == tot(cards2))
    unevenTestCases.foreach(t => testOk(t, 0, sold2, cards2))
  }

  test(
    "Should work correctly when given money and sold cards are sufficient for the wanted cards (20pts)"
  ) {
    val cards = List("aaaa", "bbb", "ccccc", "dd", "eeee", "f", "ggggggg")
    val sold = List("11111", "2", "33", "44", "5555", "666", "777")
    val bagMoney = tot(cards) - tot(sold)
    testCases.foreach(t => testOk(t, bagMoney, sold, cards))
  }
  test(
    "Should work correctly when given money and sold cards are sufficient for the wanted cards (uneven waiting time) (10pts)"
  ) {
    val cards = List("aaaa", "bbb", "ccccc", "dd", "eeee", "f", "ggggggg")
    val sold = List("11111", "2", "33", "44", "5555", "666", "777")
    val bagMoney = tot(cards) - tot(sold)
    unevenTestCases.foreach(t => testOk(t, bagMoney, sold, cards))
  }

  // --- Failures ---

  test(
    "Should return a failure when too little money is provided (no card sold) (20pts)"
  ) {
    val cards = List("aaaa", "bbb", "ccccc", "dd", "eeee", "f", "ggggggg")
    testCases.foreach(t => testFailure(t, tot(cards) - 1, Nil, cards))
    testCases.foreach(t => testFailure(t, tot(cards) - 50, Nil, cards))
  }

  test(
    "Should return a failure when too little money or sold cards are provided (20pts)"
  ) {
    val cards = List("aaaa", "bbb", "ccccc", "dd", "eeee", "f", "ggggggg")
    val sold = List("11111", "2", "33", "44", "5555", "666", "777")
    val bagMoney = tot(cards) - tot(sold)
    testCases.foreach(t => testFailure(t, bagMoney - 2, sold, cards))
  }
