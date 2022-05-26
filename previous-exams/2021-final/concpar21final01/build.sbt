course := "concpar"
assignment := "concpar21final01"
scalaVersion := "3.1.0"

scalacOptions ++= Seq("-language:implicitConversions")
libraryDependencies += "org.scalameta" %% "munit" % "1.0.0-M3" % Test

enablePlugins(PlayScala)
disablePlugins(PlayLayoutPlugin)

libraryDependencies := libraryDependencies.value.map(dep =>
  if(dep.organization == "com.typesafe.play") dep.cross(CrossVersion.for3Use2_13)
  else dep
)

val MUnitFramework = new TestFramework("munit.Framework")
testFrameworks += MUnitFramework
// Decode Scala names
testOptions += Tests.Argument(MUnitFramework, "-s")
