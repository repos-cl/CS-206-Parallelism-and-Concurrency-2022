course := "final"
assignment := "f2"
scalaVersion := "3.0.0-RC1"
scalacOptions ++= Seq("-language:implicitConversions", "-deprecation")

val akkaVersion = "2.6.0"

libraryDependencies += "org.scalameta" %% "munit" % "0.7.22"
libraryDependencies += ("com.typesafe.akka" %% "akka-actor" % akkaVersion).withDottyCompat(scalaVersion.value)
libraryDependencies += ("com.typesafe.akka" %% "akka-testkit" % akkaVersion).withDottyCompat(scalaVersion.value)

val MUnitFramework = new TestFramework("munit.Framework")
testFrameworks += MUnitFramework
// Decode Scala names
testOptions += Tests.Argument(MUnitFramework, "-s")
testSuite := "f2.F2Suite"
