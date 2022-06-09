package concpar22final04

import akka.actor.*
import akka.testkit.*
import java.util.Date
import akka.event.LoggingReceive
import akka.pattern.*
import akka.util.Timeout
import concurrent.duration.*
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

given timeout: Timeout = Timeout(200.millis)

/** Data associated with a song: a unique `id`, a `title` and an `artist`.
  */
case class Song(id: Int, title: String, artist: String)

/** An activity in a user's activity feed, representing that `userRef` is
  * listening to `songId`.
  */
case class Activity(userId: String, userName: String, songId: Int)

/** Companion object of the `User` class.
  */
object User:
  /** Messages that can be sent to User actors.
    */
  enum Protocol:
    /** Asks for a user name and id. Should be answered by a Response.Info.
      */
    case GetInfo

    /** Asks home page data. Should be answered by a Response.HomepageData.
      */
    case GetHomepageData

    /** Like song with id `songId`.
      */
    case Like(songId: Int)

    /** Unlike song with id `songId`.
      */
    case Unlike(songId: Int)

    /** Adds `subscriber` to the list of subscribers.
      */
    case Subscribe(subscriber: ActorRef)

    /** Remove `subscriber` from the list of subscribers.
      */
    case Unsubscribe(subscriber: ActorRef)

    /** Adds the activity `activity` to the activity feed. This message will be
      * sent by the users this user has subscribed to.
      */
    case AddActivity(activity: Activity)

    /** Sent when a user starts playing a song with id `songId`. The recipient
      * should notify all its subscribers to update their activity feeds by
      * sending them `AddActivity(Activity(...))` messages. No answer is
      * expected. This message is sent by external actors.
      */
    case Play(songId: Int)

    /** Asks for home page text. Should be answered by a Response.HomepageText.
      */
    case GetHomepageText

  /** Responses that can be sent back from User actors.
    */
  enum Responses:
    /** Answer to a Protocol.GetInfo message
      */
    case Info(id: String, name: String)

    /** Answer to a Protocol.GetHomepageData message
      */
    case HomepageData(songIds: List[Int], activities: List[Activity])

    /** Answer to a Protocol.GetHomepageText message
      */
    case HomepageText(result: String)

/** The `User` actor, responsible to handle `User.Protocol` messages.
  */
class User(id: String, name: String, songsStore: ActorRef) extends Actor:
  import User.*
  import User.Protocol.*
  import User.Responses.*
  import SongsStore.Protocol.*
  import SongsStore.Responses.*

  given ExecutionContext = context.system.dispatcher

  /** Liked songs, by reverse date of liking time (the last liked song must
    * be the first must be the first element of the list). Elements of this
    * list must be unique: a song can only be liked once. Liking a song
    * twice should not change the order.
    */
  var likedSongs: List[Int] = List()

  /** Users who have subscribed to this users.
    */
  var subscribers: Set[ActorRef] = Set()

  /** Activity feed, by reverse date of activity time (the last added
    * activity must be the first element of the list). Items in this list
    * should be unique by `userRef`. If a new activity with a `userRef`
    * already in the list is added, the former should be removed, so that we
    * always see the latest activity for each user we have subscribed to.
    */
  var activityFeed: List[Activity] = List()


  /** This actor's behavior. */

  override def receive: Receive = LoggingReceive {
    case GetInfo =>
      sender() ! Info(id, name)
    case GetHomepageData =>
      sender() ! HomepageData(likedSongs, activityFeed)
    case Like(songId) if !likedSongs.contains(songId) =>
      likedSongs = songId :: likedSongs
    case Unlike(songId) =>
      likedSongs = likedSongs.filter(_ != songId)
    case Subscribe(ref: ActorRef) =>
      subscribers = subscribers + ref
    case Unsubscribe(ref: ActorRef) =>
      subscribers = subscribers - ref
    case AddActivity(activity: Activity) =>
      activityFeed = activity :: activityFeed.filter(_.userId != activity.userId)
    case Play(songId) =>
      subscribers.foreach(_ ! AddActivity(Activity(id, name, songId)))
    case GetHomepageText =>
      val likedSongsFuture: Future[Songs] =
        (songsStore ? GetSongs(likedSongs)).mapTo[Songs]
      val activitySongsFuture: Future[Songs] =
        (songsStore ? GetSongs(activityFeed.map(_.songId))).mapTo[Songs]
      val response: Future[HomepageText] =
        for
          likedSongs <- likedSongsFuture;
          activitySongs <- activitySongsFuture
        yield HomepageText(f"""
          |Howdy ${name}!
          |
          |Liked Songs:
          |${likedSongs.songs
          .map(song => f"* ${song.title} by ${song.artist}")
          .mkString("\n")}
          |
          |Activity Feed:
          |${activityFeed
          .zip(activitySongs.songs)
          .map((activity, song) => f"* ${activity.userName} is listening to ${song.title} by ${song.artist}")
          .mkString("\n")}""".stripMargin.trim)
      response.pipeTo(sender())
  }

/** Objects containing the messages a songs store should handle.
  */
object SongsStore:
  /** Ask information about a list of songs by their ids.
    */
  enum Protocol:
    case GetSongs(songIds: List[Int])

  /** List of `Song` corresponding to the list of IDs given to `GetSongs`.
    */
  enum Responses:
    case Songs(songs: List[Song])

/** A mock implementation of a songs store.
  */ 
class MockSongsStore extends Actor:
  import SongsStore.Protocol.*
  import SongsStore.Responses.*
  import SongsStore.*

  val songsDB = Map(
    1 -> Song(1, "High Hopes", "Pink Floyd"),
    2 -> Song(2, "Sunny", "Boney M."),
    3 -> Song(3, "J'irai où tu iras", "Céline Dion & Jean-Jacques Goldman"),
    4 -> Song(4, "Ce monde est cruel", "Vald"),
    5 -> Song(5, "Strobe", "deadmau5"),
    6 -> Song(6, "Désenchantée", "Mylène Farmer"),
    7 -> Song(7, "Straight Edge", "Minor Threat"),
    8 -> Song(8, "Hold the line", "TOTO"),
    9 -> Song(9, "Anarchy in the UK", "Sex Pistols"),
    10 -> Song(10, "Breakfast in America", "Supertramp")
  )

  override def receive: Receive = LoggingReceive { case GetSongs(songsIds) =>
    sender() ! Songs(songsIds.map(songsDB))
  }

/////////////////////////
//        DEBUG        //
/////////////////////////

/** Infrastructure to help debugging. In sbt use `run` to execute this code.
 *  The TestKit is an actor that can send messages and check the messages it receives (or not).
 */
@main def debug() = new TestKit(ActorSystem("DebugSystem")) with ImplicitSender:
  import User.*
  import User.Protocol.*
  import User.Responses.*
  import SongsStore.Protocol.*
  import SongsStore.Responses.*

  try
    val songsStore = system.actorOf(Props(MockSongsStore()), "songsStore")
    val anita = system.actorOf(Props(User("100", "Anita", songsStore)))

    anita ! Like(6)
    expectNoMessage() // expects no message is received

    anita ! GetHomepageData
    expectMsg(HomepageData(List(6), List()))
  finally shutdown(system)
