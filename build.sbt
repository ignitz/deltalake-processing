scalaVersion := "2.12.10"
name := "deltalake-processing"
// organization := "com.yuriniitsuma"
version := "1.0"

val sparkVersion = "3.0.1"

// Spark libraries provided to intelSense on VSCode
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core",
  "org.apache.spark" %% "spark-sql",
  "org.apache.spark" %% "spark-hive"
).map(_ % sparkVersion % "provided")

// https://docs.delta.io/latest/quick-start.html
// https://mvnrepository.com/artifact/io.delta/delta-core
libraryDependencies += "io.delta" %% "delta-core" % "0.7.0"

// https://mvnrepository.com/artifact/com.typesafe.play/play-json
// Works with 2.7.4 on spark 2.4.4 scala 2.11
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.1"

// AWS
libraryDependencies += "com.amazonaws" % "aws-java-sdk-s3" % "1.11.427"

// Deduplicate conflited classes
// assemblyMergeStrategy in assembly := {
//   case PathList("META-INF", xs @ _*) => MergeStrategy.discard
//   case x                             => MergeStrategy.first
// }
// assemblyMergeStrategy in assembly := {
//   case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
//   case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
//   case "application.conf"                            => MergeStrategy.concat
//   case "unwanted.txt"                                => MergeStrategy.discard
//   case x =>
//     val oldStrategy = (assemblyMergeStrategy in assembly).value
//     oldStrategy(x)
// }

// logLevel in assembly := Level.Debug

assemblyMergeStrategy in assembly := {
  case PathList("jackson-annotations-2.10.5.jar", xs @ _*) => MergeStrategy.last
  case PathList("jackson-core-2.10.5.jar", xs @ _*)        => MergeStrategy.last
  case PathList("jackson-databind-2.10.5.jar", xs @ _*)    => MergeStrategy.last
  case PathList("jackson-dataformat-cbor-2.10.5.jar", xs @ _*) =>
    MergeStrategy.last
  case PathList("jackson-datatype-jdk8-2.10.5.jar", xs @ _*) =>
    MergeStrategy.last
  case PathList("jackson-datatype-jsr310-2.10.5.jar", xs @ _*) =>
    MergeStrategy.last
  case PathList("jackson-module-parameter-names-2.10.5.jar", xs @ _*) =>
    MergeStrategy.last
  case PathList("jackson-module-paranamer-2.10.5.jar", xs @ _*) =>
    MergeStrategy.last
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case x                                   => MergeStrategy.last
}
