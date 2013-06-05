import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "tarifsystem"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
      "org.webjars" %% "webjars-play" % "2.1.0-2",
      "org.webjars" % "bootstrap" % "2.3.2"
    // Add your project dependencies here,
    //jdbc,
    //anorm
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
