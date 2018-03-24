import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "me.andrusha",
      scalaVersion := "2.12.4",
      crossScalaVersions := Seq("2.12.4", "2.11.12"),
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Dropwizard Prometheus",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += dropwizardMetrics,
    libraryDependencies += httpd,
    libraryDependencies += logging
  )
