package concpar21final01

import play.api.test.*
import play.api.test.Helpers.*
import scala.concurrent.duration.*
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class Problem1Suite extends munit.FunSuite:
  test(
    "Retrieves grades at the end of the exam (everyone pushed something) (10pts)"
  ) {
    class Problem1Done extends Problem1:
      override def getGrade(sciper: Int): Future[Option[Grade]] =
        Future {
          Thread.sleep(100)
          Some(Grade(sciper, sciper))
        }
      override def getScipers(): Future[List[Int]] =
        Future {
          Thread.sleep(100)
          List(1, 2, 3, 4)
        }

    val expected: List[Grade] =
      List(Grade(1, 1.0), Grade(2, 2.0), Grade(3, 3.0), Grade(4, 4.0))

    (new Problem1Done).leaderboard().map { grades =>
      assertEquals(grades.toSet, expected.toSet)
    }
  }

  test("Retrieves grades mid exam (some students didn't push yet) (10pts)") {
    class Problem1Partial extends Problem1:
      override def getGrade(sciper: Int): Future[Option[Grade]] =
        Future {
          Thread.sleep(100)
          if sciper % 2 == 0 then None
          else Some(Grade(sciper, sciper))
        }
      override def getScipers(): Future[List[Int]] =
        Future {
          Thread.sleep(100)
          List(1, 2, 3, 4)
        }

    val expected: List[Grade] =
      List(Grade(1, 1.0), Grade(3, 3.0))

    (new Problem1Partial).leaderboard().map { grades =>
      assertEquals(grades.toSet, expected.toSet)
    }
  }

  test("The output list is sorted by grade (10pts)") {
    (new Problem1MockData).leaderboard().map { grades =>
      assert(grades.size >= 176)
      assert(grades.zipWithIndex.forall { case (g, i) =>
        grades.drop(i).forall(x => g.grade >= x.grade)
      })
    }
  }

  test("GitLab API calls are done in parallel (2pts)") {
    var inParallel: Boolean = false

    class Problem1Par extends Problem1MockData:
      var in: Boolean = false

      override def getGrade(sciper: Int): Future[Option[Grade]] =
        Future {
          if in then inParallel = true
          in = true
          val out = super.getGrade(sciper)
          in = false
          concurrent.Await.result(out, Duration(10, SECONDS))
        }

    (new Problem1Par).leaderboard().map { grades =>
      assert(grades.size >= 176)
      assert(inParallel)
    }
  }

  test("The IS-academia API is called exactly once (2pts)") {
    var called: Int = 0

    class Problem1Once extends Problem1MockData:
      override def getScipers(): Future[List[Int]] =
        called += 1
        super.getScipers()

    (new Problem1Once).leaderboard().map { grades =>
      assert(grades.size >= 176)
      assert(called == 1)
    }
  }
