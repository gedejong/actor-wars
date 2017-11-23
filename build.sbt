name := "robot-wars"

version := "0.1"

scalaVersion := "2.12.4"

val akkaVersion = "2.5.6"
val akkaStreamKafkaVersion = "0.17"
val akkaHttpVersion = "10.0.10"
val circeVersion = "0.8.0"
val playJsonVersion = "2.6.6"

/** Docker and deployment */
lazy val core = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      // Akka related
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream-kafka" % akkaStreamKafkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,

      // Akka HTTP
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",

      // Play-json
      "com.typesafe.play" %% "play-json" % playJsonVersion,

      // Play json -- Akka HTTP bridge
      "de.heikoseeberger" %% "akka-http-play-json" % "1.18.0",

      // Logging
      "ch.qos.logback" % "logback-classic" % "1.0.9",

      // Squants (Scala Quantities)
      "org.typelevel"  %% "squants"  % "1.3.0",

      // Scalatest
      "org.scalatest" %% "scalatest" % "3.0.4" % "test"
    )
  )
