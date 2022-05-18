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
      "-Dakka.loglevel=Info",
      "-Dakka.actor.allow-java-serialization=on"
    ),
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.0.0-M3" % Test,
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
      "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
      // SLF4J backend
      // See https://doc.akka.io/docs/akka/current/typed/logging.html#slf4j-backend
      "ch.qos.logback" % "logback-classic" % "1.2.11"
    ),
    Test / testOptions += Tests.Argument(TestFrameworks.JUnit)
  )
