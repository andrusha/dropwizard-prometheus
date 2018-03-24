import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"

  lazy val dropwizardMetrics = "io.dropwizard.metrics" % "metrics-core" % "3.1.5"

  lazy val httpd = "org.nanohttpd" % "nanohttpd" % "2.3.1"

  lazy val logging = "org.slf4j" % "slf4j-api" % "1.7.25"
}
