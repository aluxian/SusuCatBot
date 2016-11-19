name := "uos-bot"

version := "0.4.0"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  "RoundEights" at "http://maven.spikemark.net/roundeights"
)

libraryDependencies ++= Seq(
  "com.github.finagle" %% "finch-core" % "0.11.0-M4",
  "com.github.finagle" %% "finch-circe" % "0.11.0-M4",
  "com.twitter" %% "twitter-server" % "1.23.0",
  "com.github.nscala-time" %% "nscala-time" % "2.14.0",
  "org.reactivemongo" %% "reactivemongo" % "0.12.0",
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "com.roundeights" %% "hasher" % "1.2.0",
  "io.circe" %% "circe-core" % "0.5.3",
  "io.circe" %% "circe-generic" % "0.5.3",
  "io.circe" %% "circe-parser" % "0.5.3",

  // test
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)
