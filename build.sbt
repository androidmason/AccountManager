name := """PlayScalaProject"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "org.scalikejdbc" %% "scalikejdbc"         % "2.3.4",
  "ch.qos.logback"  %  "logback-classic"     % "1.1.3",
  "org.scalikejdbc" %% "scalikejdbc-config"  % "2.3.4"
  )

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

EclipseKeys.withSource := true

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator