course := "concpar"
assignment := "concpar21final02"
scalaVersion := "3.1.0"

scalacOptions ++= Seq("-language:implicitConversions", "-deprecation")
libraryDependencies += "org.scalameta" %% "munit" % "1.0.0-M3" % Test

val akkaVersion = "2.6.19"
val logbackVersion = "1.2.11"
libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    // SLF4J backend
    // See https://doc.akka.io/docs/akka/current/typed/logging.html#slf4j-backend
    "ch.qos.logback" % "logback-classic" % logbackVersion
)
fork := true
javaOptions ++= Seq("-Dakka.loglevel=Error", "-Dakka.actor.debug.receive=on")

val MUnitFramework = new TestFramework("munit.Framework")
testFrameworks += MUnitFramework
// Decode Scala names
testOptions += Tests.Argument(MUnitFramework, "-s")
