val scala3Version = "3.1.2"

enablePlugins(JmhPlugin)

lazy val root = project
  .in(file("."))
  .settings(
    name := "code",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.3",
    libraryDependencies += "junit" % "junit" % "4.13" % Test,
    libraryDependencies += "com.github.sbt" % "junit-interface" % "0.13.3" % Test,

    Test / parallelExecution := false,
    Test / testOptions += Tests.Argument(TestFrameworks.JUnit)
  )
