package f2

import akka.actor._
import akka.testkit.*
import scala.collection.mutable
import concurrent.duration.*

import F2.*

class F2Suite extends munit.FunSuite {
  import NotificationService.Protocol.*
  import NotificationService.Responses.*
  import DiscordChannel.Protocol.*
  import DiscordChannel.Responses.*

  test("Notification register (1pts)") {
    new MyTestKit {
      def tests() = {
        val actor = system.actorOf(Props[NotificationService])
        actor ! Register
        expectMsg(2.second, Registered(true))
      }
    }
  }

  test("Notification register and un-register (1pts)") {
    new MyTestKit {
      def tests() = {
        val actor = system.actorOf(Props[NotificationService])
        actor ! Register
        expectMsg(2.second, Registered(true))
        actor ! UnRegister
        expectMsg(2.second, Registered(false))
        actor ! UnRegister
        expectMsg(2.second, Registered(false))
        actor ! Register
        expectMsg(2.second, Registered(true))
        actor ! UnRegister
        expectMsg(2.second, Registered(false))
      }
    }
  }

   test("Notification notify (1pts)") {
    new MyTestKit {
      def tests() = {
        val actor = system.actorOf(Props[NotificationService])
        actor ! Register
        expectMsg(2.second, Registered(true))
        actor ! NotifyAll
        expectMsg(2.second, Notification)
        actor ! NotifyAll
        expectMsg(2.second, Notification)
        actor ! UnRegister
        expectMsg(2.second, Registered(false))
        actor ! NotifyAll
        expectNoMessage(500.millis)
        actor ! Register
        expectMsg(2.second, Registered(true))
        actor ! NotifyAll
        expectMsg(2.second, Notification)
        actor ! UnRegister
        expectMsg(2.second, Registered(false))
        actor ! NotifyAll
        expectNoMessage(500.millis)
      }
    }
  }

  test("NotifyAll from other actor (1pts)") {
    new MyTestKit {
      def tests() = {
        val actor = system.actorOf(Props[NotificationService])
        val otherActor = system.actorOf(Props[DummyActor])

        def notifyFormAllFromOtherActor() = {
          given ActorRef = otherActor
          actor ! NotifyAll
        }

        expectNoMessage(500.millis)

        actor ! Register
        expectMsg(2.second, Registered(true))

        notifyFormAllFromOtherActor()
        expectMsg(2.second, Notification)
      }
    }
  }

  test("Channel init (1pts)") {
    new MyTestKit {
      def tests() = {
        val notificationService = system.actorOf(Props[NotificationService])
        val channel = system.actorOf(Props[DiscordChannel])
        channel ! Init(notificationService)
        expectMsg(2.second, Active)
      }
    }
  }

  test("Channel post and get post (1pts)") {
    new MyTestKit {
      def tests() = {
        val notificationService = system.actorOf(Props[NotificationService])
        val channel = system.actorOf(Props[DiscordChannel])
        channel ! Init(notificationService)
        expectMsg(2.second, Active)
        channel ! Post("hello")
        channel ! GetLastPosts(1)
        expectMsg(2.second, Posts(List("hello")))
        channel ! GetLastPosts(10)
        expectMsg(2.second, Posts(List("hello")))
        channel ! GetLastPosts(0)
        expectMsg(2.second, Posts(Nil))
      }
    }
  }

  test("Channel multiple posts (1pts)") {
    new MyTestKit {
      def tests() = {
        val notificationService = system.actorOf(Props[NotificationService])
        val channel = system.actorOf(Props[DiscordChannel])
        channel ! Init(notificationService)
        expectMsg(2.second, Active)
        channel ! Post("hello")
        channel ! Post("world")
        channel ! GetLastPosts(2)
        channel ! GetLastPosts(1)
        channel ! Post("!")
        channel ! GetLastPosts(3)
        expectMsg(2.second, Posts(List("world", "hello")))
        expectMsg(2.second, Posts(List("world")))
        expectMsg(2.second, Posts(List("!", "world", "hello")))
      }
    }
  }

  test("Channel posts and notify (1pts)") {
    new MyTestKit {
      def tests() = {
        val notificationService = system.actorOf(Props[NotificationService])
        val channel = system.actorOf(Props[DiscordChannel])
        channel ! Init(notificationService)
        expectMsg(2.second, Active)
        notificationService ! Register
        expectMsg(2.second, Registered(true))
        channel ! Post("hello")
        channel ! Post("world")
        expectMsg(2.second, Notification)
        expectMsg(2.second, Notification)
      }
    }
  }

  test("Channel init twice (1pts)") {
    new MyTestKit {
      def tests() = {
        val notificationService = system.actorOf(Props[NotificationService])
        val channel = system.actorOf(Props[DiscordChannel])
        channel ! Init(notificationService)
        expectMsg(2.second, Active)
        channel ! Init(notificationService)
        expectMsg(2.second, AlreadyActive)
        channel ! Init(notificationService)
        expectMsg(2.second, AlreadyActive)
      }
    }
  }

  test("Channel not active (1pts)") {
    new MyTestKit {
      def tests() = {
        val channel1 = system.actorOf(Props[DiscordChannel])
        channel1 ! Post("hello")
        expectMsg(2.second, NotActive)

        val channel2 = system.actorOf(Props[DiscordChannel])
        channel2 ! GetLastPosts(0)
        expectMsg(2.second, NotActive)
      }
    }
  }

  abstract class MyTestKit extends TestKit(ActorSystem("TestSystem")) with ImplicitSender {
    def tests(): Unit
    try tests() finally shutdown(system)
  }

}

class DummyActor extends Actor {
  def receive: Receive = {
    case _ => ()
  }
}
