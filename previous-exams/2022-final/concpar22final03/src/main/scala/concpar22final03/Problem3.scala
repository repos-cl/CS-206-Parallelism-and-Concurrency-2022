package concpar22final03

import scala.concurrent.Future
import concurrent.ExecutionContext.Implicits.global

trait Problem3:
  val economics: Economics
  import economics.*

  /** The objective is to propose a service of deck building. People come to you with some money and
    * some cards they want to sell, and you need to return them a complete deck of the cards they
    * want.
    */
  def orderDeck(
      bag: MoneyBag,
      cardsToSell: List[Card],
      wantedDeck: List[String]
  ): Future[List[Card]] =
    Future {
      ??? // : Future[List[Card]]
    }.flatten

  /** This helper function will sell the provided list of cards and put the money on your personal
    * bank account. It returns a Future of Unit, which indicates when all sales are completed.
    */
  def sellListOfCards(cardsToSell: List[Card]): Future[Unit] =
    val moneyFromSales: List[Future[Unit]] = cardsToSell.map { c =>
      sellCard(c).flatMap(m => deposit(m).map { _ => })
    }
    Future
      .sequence(moneyFromSales)
      .map(_ => ()) // Future.sequence transforms a List[Future[A]] into a Future[List[A]]

  /** This helper function, given a list of wanted card names and assuming there is enough money in
    * the bank account, will buy (in the future) those cards, and return them.
    */
  def buyListOfCards(wantedDeck: List[String]): Future[List[Card]] =
    ???
