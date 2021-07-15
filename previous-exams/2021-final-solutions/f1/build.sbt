course := "final"
assignment := "f1"
scalaVersion := "3.0.0-RC1"
scalacOptions += "-nowarn"

enablePlugins(PlayScala)
disablePlugins(PlayLayoutPlugin)

libraryDependencies := libraryDependencies.value.map(_.withDottyCompat(scalaVersion.value))
libraryDependencies += "org.scalameta" %% "munit" % "0.7.22"

val MUnitFramework = new TestFramework("munit.Framework")
testFrameworks += MUnitFramework
// Decode Scala names
testOptions += Tests.Argument(MUnitFramework, "-s")
testSuite := "f1.F1Suite"
