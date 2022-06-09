package concpar22final03

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

trait EconomicsTest extends Economics:
  val ownedCards: collection.mutable.Set[Card] = collection.mutable.Set[Card]()
  def owned(c: Card): Boolean = ownedCards(c)
  def isMine(c: Card): Boolean = ownedCards(c)

  override def valueOf(cardName: String): Int = List(1, cardName.length).max

  /** This is a container for an exact amount of money that can be hold, spent, or put in the bank
    */
  val moneyInMoneyBag = collection.mutable.Map[MoneyBag, Int]()
  def moneyIn(m: MoneyBag): Int = moneyInMoneyBag.getOrElse(m, 0)

  /** If you sell a card, at some point in the future you will get some money (in a bag).
    */
  def sellCard(c: Card): Future[MoneyBag] =
    Future {
      Thread.sleep(sellWaitTime())
      synchronized(
        if owned(c) then
          ownedCards.remove(c)
          getMoneyBag(valueOf(c.name))
        else
          throw Exception(
            "This card doesn't belong to you or has already been sold, you can't sell it."
          )
      )
    }

  /** You can buy any "Scala: The Programming" card by providing a bag of money with the appropriate
    * amount and waiting for the transaction to take place
    */
  def buyCard(bag: MoneyBag, name: String): Future[Card] =
    Future {
      Thread.sleep(buyWaitTime())
      synchronized {
        if moneyIn(bag) != valueOf(name) then
          throw Exception(
            "You didn't provide the exact amount of money necessary to buy this card."
          )
        else moneyInMoneyBag.update(bag, 0)
        getCard(name)
      }

    }

  /** This simple bank account hold money for you. You can bring a money bag to increase your
    * account, or withdraw a money bag of any size not greater than your account's balance.
    */
  private var balance_ = initialBalance()
  def balance: Int = balance_
  def withdraw(amount: Int): Future[MoneyBag] =
    Future {
      Thread.sleep(withdrawWaitTime())
      synchronized(
        if balance_ >= amount then
          balance_ -= amount
          getMoneyBag(amount)
        else
          throw new Exception(
            "You try to withdraw more money than you have on your account"
          )
      )
    }

  def deposit(bag: MoneyBag): Future[Unit] =
    Future {
      Thread.sleep(depositWaitTime())
      synchronized {
        if moneyInMoneyBag(bag) == 0 then throw new Exception("You are depositing en empty bag!")
        else
          balance_ += moneyIn(bag)
          moneyInMoneyBag.update(bag, 0)
      }
    }

  def sellWaitTime(): Int
  def buyWaitTime(): Int
  def withdrawWaitTime(): Int
  def depositWaitTime(): Int
  def initialBalance(): Int

  def getMoneyBag(i: Int) =
    val m = MoneyBag()
    synchronized(moneyInMoneyBag.update(m, i))
    m

  def getCard(n: String): Card =
    val c = Card(n)
    synchronized(ownedCards.update(c, true))
    c
