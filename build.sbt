name := "ScalaApacheAccessLogParser"

version := "1.0"

val kafkaVersion = "0.9.0.0"

scalaVersion := "2.10.0"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

scalacOptions += "-deprecation"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.10" % "2.1.0" % "test",
  "org.apache.kafka" % "kafka-clients" % kafkaVersion,
  "org.apache.spark" %% "spark-core" % "2.1.0",
  "org.apache.spark" %% "spark-sql" % "2.1.0"
)

