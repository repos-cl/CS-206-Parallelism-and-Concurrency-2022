package concpar21final01

import play.api.{ApplicationLoader, BuiltInComponentsFromContext}
import play.api.mvc.Results.Ok
import play.api.routing.sird.*
import play.api.routing.Router
import play.api.ApplicationLoader.Context
import play.filters.HttpFiltersComponents

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

class MyApplicationLoader extends ApplicationLoader:
  def load(context: Context) =
    new MyComponents(context).application

class MyComponents(context: Context)
    extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents:

  lazy val router = Router.from { case GET(p"/") =>
    Action.async {
      (new Problem1MockData)
        .leaderboard()
        .map(leaderboardHTML)
        .map(Ok(_).as("text/html"))
    }
  }

  def leaderboardHTML(data: List[Grade]): String =
    s"""
    |<!DOCTYPE html>
    |<html>
    |  <head>
    |    <title>Leaderboard</title>
    |  </head>
    |  <body>
    |    <h1>Leaderboard:</h1>
    |    <ul>
    |      ${data
      .map { case Grade(sciper, g) =>
        val grade = "%1.2f".format(g)
        s"<li>$sciper : $grade</li>"
      }
      .mkString("\n      ")}
    |    </ul>
    |  </body>
    |</html>
    """.trim.stripMargin

class Problem1MockData extends Problem1:
  def getGrade(sciper: Int): Future[Option[Grade]] =
    Future {
      // In an actual implementation, this is where we would make a call to
      // the GitLab APIs. This mock returns a random grade after a short delay.
      Thread.sleep(15) // GitLab is pretty fast today...
      val rand = new Random(sciper)
      val grade = rand.nextInt(6).toDouble + rand.nextDouble()
      if sciper < 100000 || sciper > 999999 || sciper % 10 == 0 then None
      else Some(Grade(sciper, grade))
    }

  /** Retrieve the list of enrolled students from IS-academia
    */
  def getScipers(): Future[List[Int]] =
    Future {
      Thread.sleep(100)
      List( // A fake list of SCIPER numbers
        301425, 207372, 320658, 300217, 224523, 301068, 331020, 331095, 320270,
        320742, 299310, 300974, 322202, 343357, 302632, 343366, 320229, 269364,
        320004, 321830, 219188, 300834, 320992, 299237, 298016, 300397, 269857,
        300492, 300481, 279254, 320967, 300443, 300329, 300305, 331158, 310402,
        279067, 300682, 259825, 351616, 310869, 301215, 299481, 269375, 351249,
        310866, 351141, 301530, 361378, 351661, 351524, 311081, 331137, 332319,
        301045, 300393, 300308, 310889, 310064, 310841, 351333, 310382, 333887,
        333837, 320832, 321397, 351691, 269125, 312732, 351546, 301783, 351698,
        310775, 331388, 311139, 301992, 301578, 361760, 351174, 310298, 300666,
        259778, 301554, 301278, 301669, 321372, 311347, 321129, 351490, 321189,
        301336, 341560, 331220, 331129, 333927, 279186, 310596, 299135, 279226,
        310507, 269049, 300309, 341524, 351143, 300785, 310612, 320338, 259980,
        269952, 310397, 320246, 310959, 301454, 301835, 301802, 301649, 301170,
        301908, 351708, 321046, 361490, 311070, 351830, 311054, 311912, 301913,
        361232, 301030, 351723, 311472, 311166, 321057, 310793, 269462, 311948,
        321693, 321056, 361765, 301453, 321626, 341490, 320892, 269871, 269580,
        320199, 320908, 320830, 269071, 380542, 253768, 311204, 269127, 351073,
        341327, 301792, 299789, 361424, 301525, 311637, 321423, 279111, 330126,
        310371, 259888, 269525, 299585, 300147, 341402, 330067, 311796, 279037,
        248517, 301436, 269965, 259963, 320720, 248583, 259709, 361204, 341500,
        311803, 299981, 311832, 301088, 259649, 279183, 341760, 311844, 279079,
        390997, 311917, 390999, 361122, 301208, 311538, 272943, 361570, 390959)
    }
