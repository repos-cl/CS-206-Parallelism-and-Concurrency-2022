# Exercise Session 5

## Problem 1: Message Processing Semantics

Consider the following actor system:

```scala
enum Protocol:
  case Write(value: Int)
  case Read(requester: ActorRef)
import Protocol.*

enum Responses:
  case Answer(value: Int)
import Responses.*

class Memory extends Actor:
  var value = 0

  override def receive: Receive = {
    case Write(newValue) => value = newValue
    case Read(requester) => requester ! Answer(value)
  }

class Client(memory: ActorRef) extends Actor:
  override def receive: Receive = { case Answer(value) =>
    println(value)
  }

class MyProxy(memory: ActorRef) extends Actor:
  override def receive: Receive = { case message =>
    memory ! message
  }
```

### Problem 1.1

And the following test:

```scala
@main def problem1_1 =
  for _ <- 1 to 1000 do
    val system = ActorSystem("example")
    try
      val memory = system.actorOf(Props(Memory()))
      val client = system.actorOf(Props(Client(memory)))
      memory ! Write(1)
      memory ! Read(client)
    finally system.terminate()
```

What are the possible values printed by the `println` command in the `Client` actor? Why?

### Problem 1.2

Now, consider the following test:

```scala
@main def problem1_2 =
  for _ <- 1 to 1000 do
    val system = ActorSystem("example")
    try
      val memory = system.actorOf(Props(Memory()))
      val proxy  = system.actorOf(Props(MyProxy(memory)))
      val client = system.actorOf(Props(Client(memory)))
      proxy ! Read(client)
      memory ! Write(1)
    finally system.terminate()
```

1. What are the possible values printed by the `println` command in the `Client2` actor? Why?
2. Would the output be different if the commands annotated with `XXX` were issued in the other order?
3. What if both messages are sent through the `Proxy` actor?
