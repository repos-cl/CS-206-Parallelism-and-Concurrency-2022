# Problem 3: Futures

## Setup

Use the following commands to make a fresh clone of your repository:

```
git clone -b concpar22final03 git@gitlab.epfl.ch:lamp/student-repositories-s22/cs206-GASPAR.git concpar22final03
```

If you have issues with the IDE, try [reimporting the
build](https://gitlab.epfl.ch/lamp/cs206/-/blob/master/labs/example-lab.md#troubleshooting),
if you still have problems, use `compile` in sbt instead.

## Exercise

The famous trading cards game Scala: The Programming has recently been gaining traction.  
In this little exercise, you will propose to clients your services as a trader: Buying and selling cards in groups on demand.

You are provided in the file `Economics.scala` with an interface to handle asynchronous buying and selling of cards and management of your money. Do not modify this file. More precisely, this interface defines:

- A `Card`, which has a `name` and which you can own (`isMine == true`) or not (`isMine == false`). This is only to prevent a card from being sold or used multiple times, and you may not need it. You can find the value of a card using `valueOf`.
- A `MoneyBag`, which is a container to transport money. Similarly, the money inside a bag can only be used once. The function `moneyIn` informs you of the bag's value, should you need it.
- The function `sellCard`, which will sell a card through a `Future` and gives you back a `Future[MoneyBag]`. If you do not own the card, the `Future` will fail.
- The function `buyCard`, which will consume a given `MoneyBag` and handle you, through a `Future`, the requested card. The provided bag must contain the exact amount of money corresponding to the card's value.
- Finally, you have a bank account with the following functions: 
	- `balance`: indicates your current monetary possession
	- `withdraw`: substracts a given amount from your balance, and handles you a corresponding `Future[MoneyBag]`
	- `deposit`: consumes a moneyBag and returns a `Future[Unit]` when the balance is updated. Note that you should not deposit empty moneyBags! If you do, you will get a failure, possibly indicating that you try to deposit the same bag twice.

Your task in the exercise is to implement the function `orderDeck` in the file `Problem3.scala`. In a `Future`, start by checking that the sum of the money and the value of the cards the client gives you is large enough to buy the requested list of cards. If it is not, then the future should fail with a `NotEnoughMoneyException`.

Then, sell all provided cards and put the received moneyBags in your bank accounts by chaining asynchronously the `Futures` of `sellCard` and `deposit`. You will obtain a `List[Future[Unit]]`, which should be converted into a `Future[Unit]` (so that when this `Future` returns, all deposits have finished). Those steps are provided for you in the helper function `sellListOfCards`.

Then, do the opposite: withdraw `MoneyBags` of adequate value and use them to buy cards. Finally agregate the `List[Future[Card]]` into a `Future[List[Card]]`. You can implement those steps into the `buyListOfCards` function. Take inspiration from the given example `sellListOfCards`, and combine them in the `orderDeck` function.

Final tip: Make good use of `map`, `flatMap` and `zip` on futures.
