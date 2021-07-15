course := "final"
assignment := "f3"
scalaVersion := "3.0.0-RC1"
scalacOptions ++= Seq("-language:implicitConversions", "-deprecation")

libraryDependencies += "org.scalameta" %% "munit" % "0.7.22"

val MUnitFramework = new TestFramework("munit.Framework")
testFrameworks += MUnitFramework
// Decode Scala names
testOptions += Tests.Argument(MUnitFramework, "-s")
testSuite := "f3.F3Suite"
