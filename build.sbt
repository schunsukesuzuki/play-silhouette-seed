import com.typesafe.sbt.SbtScalariform._

import scalariform.formatter.preferences._

name := "play-silhouette-seed"

version := "5.0.0"

scalaVersion := "2.12.3"

resolvers += Resolver.jcenterRepo

<<<<<<< HEAD
resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % "5.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "5.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "5.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "5.0.0",
  "org.webjars" %% "webjars-play" % "2.6.1",
  "org.webjars" % "bootstrap" % "3.3.7-1" exclude("org.webjars", "jquery"),
  "org.webjars" % "jquery" % "3.2.1",
  "net.codingwell" %% "scala-guice" % "4.1.0",
  "com.iheart" %% "ficus" % "1.4.1",
  "com.typesafe.play" %% "play-mailer" % "6.0.1",
  "com.typesafe.play" %% "play-mailer-guice" % "6.0.1",
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.6.1-akka-2.5.x",
  "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B3",
  "com.mohiva" %% "play-silhouette-testkit" % "5.0.0" % "test",
  specs2 % Test,
  ehcache,
  guice,
  filters
=======
//resolver移植分
resolvers += "Bintary JCenter" at "http://jcenter.bintray.com"
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"


libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % "4.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "4.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0",

  "org.webjars" %% "webjars-play" % "2.5.0-2",
  "net.codingwell" %% "scala-guice" % "4.0.1",
  "com.iheart" %% "ficus" % "1.2.6",
  "com.typesafe.play" %% "play-mailer" % "5.0.0",
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.6.0-akka-2.4.x",

//https://github.com/enragedginger/akka-quartz-scheduler
//  "com.enragedginger" %% "akka-quartz-scheduler" % "1.6.1-akka-2.5.x",

  "com.adrianhurt" %% "play-bootstrap" % "1.0-P25-B3",
  "com.mohiva" %% "play-silhouette-testkit" % "4.0.0" % "test",
  specs2 % Test,
  cache,
  filters,

//DI移植分
  jdbc,
  ws,

  "com.google.inject" % "guice" % "4.1.0",

  "org.scalatestplus.play"  %% "scalatestplus-play"             % "2.0.0-M1"  % Test,
  "org.scala-lang"          % "scala-reflect"                   % scalaVersion.value,
  "org.hamcrest"            % "hamcrest-all"                    % "1.3"       % Test,
  "org.mockito"             % "mockito-core"                    % "1.10.19"   % Test,
  "com.github.nscala-time"  %% "nscala-time"                    % "2.16.0",
  // 2.5.1
  "org.scalikejdbc"         %% "scalikejdbc"                    % "2.5.2",
  // 2.5.1  3.2.1
  "org.scalikejdbc"         %% "scalikejdbc-test"               % "2.5.2",
  // 2.5.1
  "org.scalikejdbc"         %% "scalikejdbc-config"             % "2.5.1",
  // 2.5.+  -scalikejdbc-3.2
  "org.scalikejdbc"         %% "scalikejdbc-play-initializer"   % "2.5.+",
  "org.scalikejdbc"         %% "scalikejdbc-play-dbapi-adapter" % "2.5.+",
  "org.skinny-framework"    %% "skinny-orm"                     % "2.3.5",
  // 1.4.197
  "com.h2database"          %  "h2"                             % "1.4.197",

  "org.flywaydb"            %% "flyway-play"                    % "3.0.1",
  "mysql"                   % "mysql-connector-java"            % "6.0.5",
  "play-circe" %% "play-circe" % "2.5-0.7.0",

  "org.mariadb.jdbc" % "mariadb-java-client" % "2.2.3", 

// better-files
//https://mvnrepository.com/artifact/com.github.pathikrit/better-files_2.11/3.5.0
  "com.github.pathikrit" %% "better-files" % "3.5.0",
  "com.github.pathikrit"  %% "better-files-akka"  % "3.5.0"

//akka

//  "com.typesafe.akka" %% "akka-actor" % "2.5.12",
//  "com.typesafe.akka" %% "akka-testkit" % "2.5.12" % Test,
//  "com.typesafe.akka" %% "akka-stream" % "2.5.12",
//  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.12" % Test,
//  "com.typesafe.akka" %% "akka-http" % "10.1.1",
//  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.1" % Test
>>>>>>> recommit
)


val circeVersion = "0.9.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)


lazy val root = (project in file(".")).enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator

routesImport += "utils.route.Binders._"

// https://github.com/playframework/twirl/issues/105
TwirlKeys.templateImports := Seq()

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  //"-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  // Play has a lot of issues with unused imports and unsued params
  // https://github.com/playframework/playframework/issues/6690
  // https://github.com/playframework/twirl/issues/105
  "-Xlint:-unused,_"
)

//********************************************************
// Scalariform settings
//********************************************************

defaultScalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(FormatXml, false)
  .setPreference(DoubleIndentClassDeclaration, false)
  .setPreference(DanglingCloseParenthesis, Preserve)

flywayUrl :="jdbc:mariadb://127.0.0.1:3306/spetstore?characterEncoding=utf8"

//"jdbc:mysql://localhost/spetstore?autoReconnect=true&useSSL=false"

flywayUser := "admin"

flywayPassword := "admin"

flywayLocations := Seq("filesystem:conf/db/migration/default")






