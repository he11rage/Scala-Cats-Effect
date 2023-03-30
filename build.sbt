ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

libraryDependencies += "org.typelevel" %% "cats-effect" % "3.4.8"

libraryDependencies += "org.typelevel" %% "cats-core" % "2.9.0"


lazy val root = (project in file("."))
  .settings(
    name := "cats-effect"
  )
