package concpar21final02

import akka.actor.*
import akka.testkit.*
import scala.collection.mutable
import concurrent.duration.*

import Problem2.*

class Problem2Suite extends munit.FunSuite:
  import NotificationService.Protocol.*
  import NotificationService.Responses.*
  import DiscordChannel.Protocol.*
  import DiscordChannel.Responses.*

  test("Notification register (1pts)") {
    new MyTestKit:
      def tests() =
        val actor = system.actorOf(Props[NotificationService]())
        actor ! Register
        expectMsg(Registered(true))
  }

  test("Notification register and un-register (1pts)") {
    new MyTestKit:
      def tests() =
        val actor = system.actorOf(Props[NotificationService]())
        actor ! Register
        expectMsg(Registered(true))
        actor ! UnRegister
        expectMsg(Registered(false))
        actor ! UnRegister
        expectMsg(Registered(false))
        actor ! Register
        expectMsg(Registered(true))
        actor ! UnRegister
        expectMsg(Registered(false))
  }

  test("Notification notify (1pts)") {
    new MyTestKit:
      def tests() =
        val actor = system.actorOf(Props[NotificationService]())
        actor ! Register
        expectMsg(Registered(true))
        actor ! NotifyAll
        expectMsg(Notification)
        actor ! NotifyAll
        expectMsg(Notification)
        actor ! UnRegister
        expectMsg(Registered(false))
        actor ! NotifyAll
        expectNoMessage()
        actor ! Register
        expectMsg(Registered(true))
        actor ! NotifyAll
        expectMsg(Notification)
        actor ! UnRegister
        expectMsg(Registered(false))
        actor ! NotifyAll
        expectNoMessage()
  }

  test("NotifyAll from other actor (1pts)") {
    new MyTestKit:
      def tests() =
        val actor = system.actorOf(Props[NotificationService]())
        val otherActor = system.actorOf(Props[DummyActor]())

        def notifyFormAllFromOtherActor() =
          given ActorRef = otherActor
          actor ! NotifyAll

        expectNoMessage()

        actor ! Register
        expectMsg(Registered(true))

        notifyFormAllFromOtherActor()
        expectMsg(Notification)
  }

  test("Channel init (1pts)") {
    new MyTestKit:
      def tests() =
        val notificationService = system.actorOf(Props[NotificationService]())
        val channel = system.actorOf(Props[DiscordChannel]())
        channel ! Init(notificationService)
        expectMsg(Active)
  }

  test("Channel post and get post (1pts)") {
    new MyTestKit:
      def tests() =
        val notificationService = system.actorOf(Props[NotificationService]())
        val channel = system.actorOf(Props[DiscordChannel]())
        channel ! Init(notificationService)
        expectMsg(Active)
        channel ! Post("hello")
        channel ! GetLastPosts(1)
        expectMsg(Posts(List("hello")))
        channel ! GetLastPosts(10)
        expectMsg(Posts(List("hello")))
        channel ! GetLastPosts(0)
        expectMsg(Posts(Nil))
  }

  test("Channel multiple posts (1pts)") {
    new MyTestKit:
      def tests() =
        val notificationService = system.actorOf(Props[NotificationService]())
        val channel = system.actorOf(Props[DiscordChannel]())
        channel ! Init(notificationService)
        expectMsg(Active)
        channel ! Post("hello")
        channel ! Post("world")
        channel ! GetLastPosts(2)
        channel ! GetLastPosts(1)
        channel ! Post("!")
        channel ! GetLastPosts(3)
        expectMsg(Posts(List("world", "hello")))
        expectMsg(Posts(List("world")))
        expectMsg(Posts(List("!", "world", "hello")))
  }

  test("Channel posts and notify (1pts)") {
    new MyTestKit:
      def tests() =
        val notificationService = system.actorOf(Props[NotificationService]())
        val channel = system.actorOf(Props[DiscordChannel]())
        channel ! Init(notificationService)
        expectMsg(Active)
        notificationService ! Register
        expectMsg(Registered(true))
        channel ! Post("hello")
        channel ! Post("world")
        expectMsg(Notification)
        expectMsg(Notification)
  }

  test("Channel init twice (1pts)") {
    new MyTestKit:
      def tests() =
        val notificationService = system.actorOf(Props[NotificationService]())
        val channel = system.actorOf(Props[DiscordChannel]())
        channel ! Init(notificationService)
        expectMsg(Active)
        channel ! Init(notificationService)
        expectMsg(AlreadyActive)
        channel ! Init(notificationService)
        expectMsg(AlreadyActive)
  }

  test("Channel not active (1pts)") {
    new MyTestKit:
      def tests() =
        val channel1 = system.actorOf(Props[DiscordChannel]())
        channel1 ! Post("hello")
        expectMsg(NotActive)

        val channel2 = system.actorOf(Props[DiscordChannel]())
        channel2 ! GetLastPosts(0)
        expectMsg(NotActive)
  }

  abstract class MyTestKit
      extends TestKit(ActorSystem("TestSystem"))
      with ImplicitSender:
    def tests(): Unit
    try tests()
    finally shutdown(system)

class DummyActor extends Actor:
  def receive: Receive = { case _ =>
    ()
  }
