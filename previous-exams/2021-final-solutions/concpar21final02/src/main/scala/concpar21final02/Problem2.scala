package concpar21final02

import akka.actor.*
import scala.collection.mutable
import akka.testkit.*

object Problem2:

  //////////////////////////////
  //   NOTIFICATION SERVICE   //
  //////////////////////////////

  object NotificationService:
    enum Protocol:
      /** Notify all registered actors */
      case NotifyAll

      /** Register the actor that sent the `Register` request */
      case Register //
      /** Un-register the actor that sent the `Register` request */
      case UnRegister

    enum Responses:
      /** Message sent to an actor when it is notified */
      case Notification

      /** Response sent to an actor after a `Register` or `UnRegister` */
      case Registered(registered: Boolean)

  class NotificationService extends Actor:
    import NotificationService.Protocol.*
    import NotificationService.Responses.*

    private val registeredUsers = mutable.Set.empty[ActorRef]

    def receive: Receive = {

      case Register =>
        registeredUsers += sender()
        sender() ! Registered(true)
      case UnRegister =>
        registeredUsers -= sender()
        sender() ! Registered(false)
      case NotifyAll =>
        for user <- registeredUsers do user ! Notification
    }

  /////////////////////////
  //   DISCORD CHANNEL   //
  /////////////////////////

  object DiscordChannel:

    enum Protocol:

      /** Post a message in the channel */
      case Post(msg: String)

      /** Ask for the list of most recent posts starting from the most recent
        * one. The list must have at most `limit` posts.
        */
      case GetLastPosts(limit: Int)

      /** Activates the service channel using the provided notification service.
        */
      case Init(notificationService: ActorRef)

    enum Responses:

      /** Response to `GetLastPosts` if active */
      case Posts(msgs: List[String])

      /** Response after `Init` if non-active */
      case Active

      /** Response `Post` and `GetLastPosts` if non-active */
      case NotActive

      /** Response after `Init` if active */
      case AlreadyActive

  class DiscordChannel extends Actor:
    import DiscordChannel.Protocol.*
    import DiscordChannel.Responses.*
    import NotificationService.Protocol.*

    private var messages: List[String] = Nil

    def receive: Receive = nonActive

    def nonActive: Receive = {

      case Init(service) =>
        context.become(active(service))
        sender() ! Active
      case Post(_) | GetLastPosts(_) =>
        sender() ! NotActive
    }

    def active(notificationService: ActorRef): Receive = {

      case Post(msg) =>
        messages = msg :: messages
        notificationService ! NotifyAll
      case GetLastPosts(limit) =>
        sender() ! Posts(messages.take(limit))
      case Init(_) =>
        sender() ! AlreadyActive
    }

/////////////////////////
//        DEBUG        //
/////////////////////////

/** Infrastructure to help debugging. In sbt use `run` to execute this code. The
  * TestKit is an actor that can send messages and check the messages it
  * receives (or not).
  */
@main def debug() = new TestKit(ActorSystem("DebugSystem")) with ImplicitSender:
  import Problem2.*
  import DiscordChannel.Protocol.*
  import DiscordChannel.Responses.*
  import NotificationService.Protocol.*
  import NotificationService.Responses.*
  import concurrent.duration.*

  try
    val notificationService = system.actorOf(Props[NotificationService]())
    val channel = system.actorOf(Props[DiscordChannel]())

    notificationService ! NotifyAll
    expectNoMessage(
      200.millis
    ) // expects no message is received in the next 200 milliseconds

    notificationService ! Register
    expectMsg(
      200.millis,
      Registered(true)
    ) // expects to receive `Registered(true)` in the next 200 milliseconds

  finally shutdown(system)
