course := "final"
assignment := "f4"

scalaVersion := "3.0.0-RC1"
scalacOptions ++= Seq("-language:implicitConversions", "-deprecation")
libraryDependencies ++= Seq(
  ("org.apache.spark" %% "spark-core" % "3.2.0-SNAPSHOT").withDottyCompat(scalaVersion.value),
)

// Contains Spark 3 snapshot built against 2.13: https://github.com/smarter/spark/tree/scala-2.13
resolvers += "Spark Snapshots Copy" at "https://scala-webapps.epfl.ch/artifactory/spark-snapshot/"

libraryDependencies += "org.scalameta" %% "munit" % "0.7.22"


val MUnitFramework = new TestFramework("munit.Framework")
testFrameworks += MUnitFramework
// Decode Scala names
testOptions += Tests.Argument(MUnitFramework, "-s")

testSuite := "f4.F4Suite"

// Without forking, ctrl-c doesn't actually fully stop Spark
fork in run := true
fork in Test := true
