import akka.actor.*
import akka.testkit.TestKit

object Soldier:
  // The different messages that can be sent between the actors:
  enum Protocol:

    // The recipient should die.
    case Death

    // The recipient should update its next reference.
    case Next(next: ActorRef)

    // The recipient should act.
    case Act

class Soldier(number: Int) extends Actor:
  import Soldier.*
  import Protocol.*

  def receive: Receive = behavior(None, None, false)

  def behavior(
      next: Option[ActorRef],
      killer: Option[ActorRef],
      mustAct: Boolean
  ): Receive = {

    case Death =>
      next match
        case Some(myNext) =>
          sender() ! Next(myNext)
          myNext ! Act
          println("Soldier " + number + " dies.")
          self ! PoisonPill

        case None =>
          context.become(
            behavior(next = None, killer = Some(sender()), mustAct = mustAct)
          )

    case Next(newNext) =>
      if newNext == self then println("Soldier " + number + " is last !")
      else if !killer.isEmpty then
        killer.get ! Next(newNext)
        newNext ! Act
        println("Soldier " + number + " dies.")
        self ! PoisonPill
      else if mustAct then
        newNext ! Death
        context.become(behavior(next = None, killer = None, mustAct = false))
      else
        context.become(
          behavior(next = Some(newNext), killer = None, mustAct = false)
        )

    case Act =>
      next match
        case Some(myNext) =>
          myNext ! Death
          context.become(
            behavior(next = None, killer = killer, mustAct = false)
          )

        case None =>
          context.become(behavior(next = None, killer = killer, mustAct = true))
  }

@main def problem2(n: Int) = new TestKit(ActorSystem()):
  import Soldier.*
  import Soldier.Protocol.*

  // Initialization
  require(n >= 1)

  // Creation of the actors.
  val actors = Seq.tabulate(n)(
    (i: Int) => system.actorOf(Props(classOf[Soldier], i), "Soldier" + i)
  )

  // Inform all actors of the next actor in the circle.
  for i <- 0 to (n - 2) do actors(i) ! Next(actors(i + 1))
  actors(n - 1) ! Next(actors(0))

  // Inform the first actor to start acting.
  actors(0) ! Act
