package concpar21final01

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Grade(sciper: Int, grade: Double)

trait Problem1:
  /** Retrieve the list of student grades, sorted such that maximum grades
    * appear at the head of the list.
    */
  def leaderboard(): Future[List[Grade]] =

    getScipers()
      .flatMap { scipers =>
        Future.sequence(scipers.map(getGrade))
      }
      .map(_.flatten.sortBy(_.grade).reverse)

  /** Retrieve a student's grade using GitLab's API. The result is wrapped in an
    * option, where `Future(None)` indicates either:
    *   - the student is not registered to the class
    *   - the student did not push his/her solution to GitLab
    */
  def getGrade(sciper: Int): Future[Option[Grade]]

  /** Retrieve the list of enrolled students from IS-academia
    */
  def getScipers(): Future[List[Int]]
