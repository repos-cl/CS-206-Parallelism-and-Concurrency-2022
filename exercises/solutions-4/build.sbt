val scala3Version = "3.1.2"
val akkaVersion = "2.6.19"

lazy val root = project
  .in(file("."))
  .settings(
    name := "code",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    fork := true,
    javaOptions ++= Seq(
      "-Dakka.loglevel=Debug",
      "-Dakka.actor.debug.receive=on"
    ),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
      "junit" % "junit" % "4.13" % Test,
      "com.github.sbt" % "junit-interface" % "0.13.3" % Test
    ),
    Test / testOptions += Tests.Argument(TestFrameworks.JUnit)
  )
