import scala.util.{Try, Success, Failure}
import scala.concurrent.ExecutionContext
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicReference
import akka.actor.*
import akka.testkit.*
import akka.event.LoggingReceive

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

@main def problem1_1 =
  for _ <- 1 to 1000 do
    val system = ActorSystem("example")
    try
      val memory = system.actorOf(Props(Memory()))
      val client = system.actorOf(Props(Client(memory)))
      memory ! Read(client)
      memory ! Write(1)
    finally system.terminate()

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
