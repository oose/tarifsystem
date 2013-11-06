import play.Project._

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.0",
  "org.webjars" % "bootstrap" % "2.3.2"
)

name := "Tarifsystem"

version := "1.0-SNAPSHOT"

playScalaSettings

val TarifSystem = project.in(file("."))





