import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "me.andrusha",
      scalaVersion := "2.12.4",
      crossScalaVersions := Seq("2.12.4", "2.11.12"),
      version      := "0.1.0"
    )),
    name := "Dropwizard Prometheus",
    description := "Dropwizard metrics exporter in Prometheus format",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += dropwizardMetrics,
    libraryDependencies += httpd,
    libraryDependencies += logging
  )

useGpg := true
pgpSecretRing := file("~/.gnupg/pubring.gpg")

pomIncludeRepository := { _ => false }
publishMavenStyle := true

licenses := Seq("MIT License" -> url("https://opensource.org/licenses/MIT"))
homepage := Some(url("https://github.com/andrusha/dropwizard-prometheus"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/andrusha/dropwizard-prometheus"),
    "scm:git@github.com:andrusha/dropwizard-prometheus.git"
  )
)

developers := List(
  Developer(
    id    = "andrusha",
    name  = "Andrew Korzhuev",
    email = "korzhuev@andrusha.me",
    url   = url("https://github.com/andrusha")
  )
)

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

