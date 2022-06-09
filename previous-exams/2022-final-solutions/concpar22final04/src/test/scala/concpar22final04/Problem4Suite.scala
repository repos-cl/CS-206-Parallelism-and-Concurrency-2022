package concpar22final04

import akka.actor.*
import akka.testkit.*
import akka.pattern.*
import akka.util.Timeout
import concurrent.duration.*
import User.Protocol.*
import User.Responses.*
import SongsStore.Protocol.*
import SongsStore.Responses.*
import scala.util.{Try, Success, Failure}
import com.typesafe.config.ConfigFactory
import java.util.Date
import scala.util.Random

class Problem4Suite extends munit.FunSuite:
//---
  Random.setSeed(42178263)
/*+++
  Random.setSeed(42)
++*/

  test("after receiving GetInfo, should answer with Info (20pts)") {
    new MyTestKit:
      def tests() =
        ada ! GetInfo
        expectMsg(Info("1", "Ada"))
  }

  test("after receiving GetHomepageData, should answer with the correct HomepageData when there is no liked songs and no activity items (30pts)") {
    new MyTestKit:
      def tests() =
        ada ! GetHomepageData
        expectMsg(HomepageData(List(), List()))
  }

  test("after receiving Like(1), should add 1 to the list of liked songs (20pts)") {
    new MyTestKit:
      def tests() =
        ada ! Like(1)
        expectNoMessage()
        ada ! GetHomepageData
        expectMsg(HomepageData(List(1), List()))
  }

  test(
    "after receiving Like(1) and then Like(2), the list of liked songs should start with List(2, 1) (20pts)"
  ) {
    new MyTestKit:
      def tests() =
        ada ! Like(1)
        expectNoMessage()
        ada ! Like(2)
        expectNoMessage()
        ada ! GetHomepageData
        expectMsg(HomepageData(List(2, 1), List()))
  }

  test(
    "after receiving Like(1) and then Like(1), song 1 should be in the list of liked songs only once (10pts)"
  ) {
    new MyTestKit:
      def tests() =
        ada ! Like(1)
        expectNoMessage()
        ada ! Like(1)
        expectNoMessage()
        ada ! GetHomepageData
        expectMsg(HomepageData(List(1), List()))
  }

  test(
    "after receiving Like(1), Like(2) and then Like(1), the list of liked songs should start with List(2, 1) (10pts)"
  ) {
    new MyTestKit:
      def tests() =
        ada ! Like(1)
        expectNoMessage()
        ada ! Like(2)
        expectNoMessage()
        ada ! Like(1)
        expectNoMessage()
        ada ! GetHomepageData
        expectMsg(HomepageData(List(2, 1), List()))
  }

  test(
    "after receiving Like(1), Unlike(1) and then Unlike(1), the list of liked songs should not contain song 1 (10pts)"
  ) {
    new MyTestKit:
      def tests() =
        ada ! Like(1)
        expectNoMessage()
        ada ! Like(2)
        expectNoMessage()
        ada ! Like(1)
        expectNoMessage()
        ada ! GetHomepageData
        expectMsg(HomepageData(List(2, 1), List()))
  }

  test(
    "after receiving Subscribe(aUser) and then Play(5), should send AddActivity(Activity(\"1\", 5)) to aUser (20pts)"
  ) {
    new MyTestKit:
      def tests() =
        ada ! Subscribe(self)
        expectNoMessage()
        ada ! Play(5)
        expectMsg(AddActivity(Activity("1", "Ada", 5)))
  }

  test(
    "after receiving Subscribe(aUser), Subscribe(bUser) and then Play(5), should send AddActivity(Activity(\"1\", 5)) to aUser (10pts)"
  ) {
    new MyTestKit:
      def tests() =
        ada ! Subscribe(self)
        expectNoMessage()
        val donald = new TestProbe(system)
        ada ! Subscribe(donald.ref)
        expectNoMessage()
        ada ! Play(5)
        expectMsg(AddActivity(Activity("1", "Ada", 5)))
        donald.expectMsg(AddActivity(Activity("1", "Ada", 5)))
  }

  test(
    "after receiving Subscribe(aUser), Subscribe(aUser) and then Play(5), should send AddActivity(Activity(\"1\", 5)) to aUser only once (10pts)"
  ) {
    new MyTestKit:
      def tests() =
        ada ! Subscribe(self)
        expectNoMessage()
        ada ! Subscribe(self)
        expectNoMessage()
        ada ! Play(5)
        expectMsg(AddActivity(Activity("1", "Ada", 5)))
        expectNoMessage()
  }

  test(
    "after receiving Subscribe(aUser), Unsubscribe(aUser) and then Play(5), should not send AddActivity(Activity(\"1\", 5)) to aUser (10pts)"
  ) {
    new MyTestKit:
      def tests() =
        ada ! Subscribe(self)
        expectNoMessage()
        ada ! Play(5)
        expectMsg(AddActivity(Activity("1", "Ada", 5)))
        ada ! Unsubscribe(self)
        expectNoMessage()
        ada ! Play(5)
        expectNoMessage()
  }

  test(
    "after receiving AddActivity(Activity(\"1\", 5)), Activity(\"1\", 5) should be in the activity feed (10pts)"
  ) {
    new MyTestKit:
      def tests() =
        ada ! AddActivity(Activity("0", "Self", 5))
        expectNoMessage()
        ada ! GetHomepageData
        expectMsg(HomepageData(List(), List(Activity("0", "Self", 5))))
  }

  test(
    "after receiving AddActivity(Activity(\"1\", 5)) and AddActivity(Activity(\"1\", 6)), Activity(\"1\", 6) should be in the activity feed and Activity(\"1\", 5) should not (10pts)"
  ) {
    new MyTestKit:
      def tests() =
        ada ! AddActivity(Activity("0", "Self", 5))
        expectNoMessage()
        ada ! AddActivity(Activity("0", "Self", 6))
        expectNoMessage()
        ada ! GetHomepageData
        expectMsg(HomepageData(List(), List(Activity("0", "Self", 6))))
  }

  test(
    "after receiving GetHomepageText, should answer with a result containing \"Howdy $name!\" where $name is the user's name (10pts)"
  ) {
    new MyTestKit:
      def tests() =
        val name = Random.alphanumeric.take(5).mkString
        val randomUser = system.actorOf(Props(classOf[User], "5", name, songsStore), "user-5")
        randomUser ! GetHomepageText
        expectMsgClass(classOf[HomepageText]).result.contains(f"Howdy $name!")
  }

  test(
    "after receiving GetHomepageText, should answer with the correct names of liked songs (1) (10pts)"
  ) {
    new MyTestKit:
      def tests() =
        ada ! Like(8)
        expectNoMessage()
        ada ! Like(3)
        expectNoMessage()
        ada ! Like(2)
        expectNoMessage()
        ada ! GetHomepageText
        assertEquals(
          expectMsgClass(classOf[HomepageText]).result.linesIterator
            .drop(2)
            .take(4)
            .mkString("\n")
            .trim,
          """
          |Liked Songs:
          |* Sunny by Boney M.
          |* J'irai où tu iras by Céline Dion & Jean-Jacques Goldman
          |* Hold the line by TOTO
          """.stripMargin.trim
        )
  }

  test(
    "after receiving GetHomepageText, should answer with the correct names of liked songs (2) (10pts)"
  ) {
    new MyTestKit:
      def tests() =
        ada ! Like(9)
        expectNoMessage()
        ada ! Like(7)
        expectNoMessage()
        ada ! GetHomepageText
        assertEquals(
          expectMsgClass(classOf[HomepageText]).result.linesIterator
            .drop(2)
            .take(3)
            .mkString("\n")
            .trim,
          """
          |Liked Songs:
          |* Straight Edge by Minor Threat
          |* Anarchy in the UK by Sex Pistols
          """.stripMargin.trim
        )
  }

  test(
    "after receiving GetHomepageText, should answer with the correct activity feed (1) (10pts)"
  ) {
    new MyTestKit:
      def tests() =
        bob ! Subscribe(ada)
        expectNoMessage()
        carol ! Subscribe(ada)
        expectNoMessage()
        donald ! Subscribe(ada)
        expectNoMessage()
        bob ! Play(3)
        expectNoMessage()
        carol ! Play(8)
        expectNoMessage()
        ada ! GetHomepageText
        assertEquals(
          expectMsgClass(classOf[HomepageText]).result.linesIterator
            .drop(4)
            .take(10)
            .mkString("\n")
            .trim,
          """
          |Activity Feed:
          |* Carol is listening to Hold the line by TOTO
          |* Bob is listening to J'irai où tu iras by Céline Dion & Jean-Jacques Goldman
          """.stripMargin.trim
        )
  }

  test(
    "after receiving GetHomepageText, should answer with the correct activity feed (2) (10pts)"
  ) {
    new MyTestKit:
      def tests() =
        bob ! Subscribe(ada)
        expectNoMessage()
        carol ! Subscribe(ada)
        expectNoMessage()
        donald ! Subscribe(ada)
        expectNoMessage()
        bob ! Play(9)
        expectNoMessage()
        carol ! Play(10)
        expectNoMessage()
        donald ! Play(6)
        expectNoMessage()
        bob ! Play(7)
        expectNoMessage()
        ada ! GetHomepageText
        assertEquals(
          expectMsgClass(classOf[HomepageText]).result.linesIterator
            .drop(4)
            .take(10)
            .mkString("\n")
            .trim,
          """
          |Activity Feed:
          |* Bob is listening to Straight Edge by Minor Threat
          |* Donald is listening to Désenchantée by Mylène Farmer
          |* Carol is listening to Breakfast in America by Supertramp
          """.stripMargin.trim
        )
  }

  test(
    "after receiving GetHomepageText, should answer with the correct text (full test) (10pts)"
  ) {
    new MyTestKit:
      def tests() =
        ada ! Like(1)
        expectNoMessage()
        ada ! Like(2)
        expectNoMessage()
        bob ! Subscribe(ada)
        expectNoMessage()
        carol ! Subscribe(ada)
        expectNoMessage()
        donald ! Subscribe(ada)
        expectNoMessage()
        donald ! Play(3)
        expectNoMessage()
        bob ! Play(4)
        expectNoMessage()
        carol ! Play(5)
        expectNoMessage()
        ada ! GetHomepageText
        assertEquals(
          expectMsgClass(classOf[HomepageText]).result.linesIterator
            .mkString("\n")
            .trim,
          """
          |Howdy Ada!
          |
          |Liked Songs:
          |* Sunny by Boney M.
          |* High Hopes by Pink Floyd
          |
          |Activity Feed:
          |* Carol is listening to Strobe by deadmau5
          |* Bob is listening to Ce monde est cruel by Vald
          |* Donald is listening to J'irai où tu iras by Céline Dion & Jean-Jacques Goldman
          """.stripMargin.trim
        )
  }

  abstract class MyTestKit
      extends TestKit(ActorSystem("TestSystem"))
      with ImplicitSender:
    val songsStore = system.actorOf(Props(MockSongsStore()), "songsStore")
    def makeAda() = system.actorOf(Props(classOf[User], "1", "Ada", songsStore), "user-1")
    val ada = makeAda()
    val bob = system.actorOf(Props(classOf[User], "2", "Bob", songsStore), "user-2")
    val carol = system.actorOf(Props(classOf[User], "3", "Carol", songsStore), "user-3")
    val donald = system.actorOf(Props(classOf[User], "4", "Donald", songsStore), "user-4")
    def tests(): Unit
    try tests()
    finally shutdown(system)
