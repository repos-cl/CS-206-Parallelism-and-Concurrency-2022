package concpar22final03

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

trait Economics:

  /** A trading card from the game Scala: The Programming. We can own a card, but once don't
    * anymore.
    */
  final class Card(val name: String)
  def isMine(c: Card): Boolean

  /** This function uses the best available database to return the sell value of a card on the
    * market.
    */
  def valueOf(cardName: String): Int = List(1, cardName.length).max

  /** This method represents an exact amount of money that can be hold, spent, or put in the bank
    */
  final class MoneyBag()
  def moneyIn(m: MoneyBag): Int

  /** If you sell a card, at some point in the future you will get some money (in a bag).
    */
  def sellCard(c: Card): Future[MoneyBag]

  /** You can buy any "Scala: The Programming" card by providing a bag of money with the appropriate
    * amount and waiting for the transaction to take place. You will own the returned card.
    */
  def buyCard(money: MoneyBag, name: String): Future[Card]

  /** This simple bank account holds money for you. You can bring a money bag to increase your
    * account's balance, or withdraw a money bag of any size not greater than your account's
    * balance.
    */
  def balance: Int
  def withdraw(amount: Int): Future[MoneyBag]
  def deposit(bag: MoneyBag): Future[Unit]

  class NotEnoughMoneyException extends Exception("Not enough money provided to buy those cards")