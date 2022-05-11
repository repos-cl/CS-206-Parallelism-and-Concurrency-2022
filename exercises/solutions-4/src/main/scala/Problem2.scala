import akka.actor.{Actor, Props, ActorSystem, ActorRef, ActorLogging}
import akka.testkit.{TestKit, ImplicitSender}
import akka.event.LoggingReceive

// Run using `sbt "runMain problem2"`

/** Type of messages exchanged between our Actors.
  *
  * Note: enumerations are the Scala 3 idiomatic syntax to define algebraic data
  * types (ADTs). The code below is desugared to something equivalent to:
  *
  * ```
  * trait Message
  * case class Request(computation: () => Unit) extends Message
  * object Ready extends Message
  * ```
  *
  * which is the syntax used in the lecture videos.
  *
  * Read also:
  *   - Translation of Enums and ADTs:
  *     https://docs.scala-lang.org/scala3/reference/enums/desugarEnums.html
  *   - Enums slides from CS210:
  *     https://gitlab.epfl.ch/lamp/cs210/-/blob/master/slides/progfun1-4-4.pdf
  */
enum Message:
  case Request(computation: () => Unit)
  case Ready
import Message.*

class Coordinator extends Actor:
  var availableWorkers: List[ActorRef] = Nil
  var pendingRequests: List[Request] = Nil

  override def receive = LoggingReceive {
    case Ready =>
      if pendingRequests.isEmpty then
        availableWorkers = availableWorkers :+ sender()
      else
        val request = pendingRequests.head
        pendingRequests = pendingRequests.tail
        sender() ! request
    case request: Request =>
      availableWorkers match
        case worker :: rest =>
          worker ! request
          availableWorkers = rest
        case Nil =>
          pendingRequests = pendingRequests :+ request
  }

class Worker(coordinator: ActorRef) extends Actor:
  coordinator ! Ready

  override def receive: Receive = LoggingReceive { case Request(f) =>
    f()
    coordinator ! Ready
  }

@main def problem2 = new TestKit(ActorSystem("coordinator-workers"))
  with ImplicitSender:
  try
    val coordinator = system.actorOf(Props(Coordinator()), "coordinator")
    val workers = Seq.tabulate(4)(i =>
      system.actorOf(Props(Worker(coordinator)), f"worker$i")
    )

    // Now, clients should be able to send requests to the coordinator…
    coordinator ! Request(() => println(3 + 5))
    coordinator ! Request(() => println(67 * 3))
    // And so on...
  finally shutdown(system)
