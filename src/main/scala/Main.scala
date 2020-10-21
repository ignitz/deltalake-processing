package deltaprocessing

import org.apache.log4j.Logger

import cloud_handler.AWS_EMR

// Play Framework JSON types for Scala
import play.api.libs.json.Json

object Main extends App {
  lazy val logger = Logger.getLogger(getClass().getName())

  val LOCAL = if (new java.io.File(".development").exists) true else false

  if (args.length == 0) {
    println("Need JSON String as parameter")
  }

  val jsonPayload = args(0)
  val jsonInput = Json.parse(jsonPayload)
  val service: String = jsonInput("service").as[String]

  service match {
    case "emr" => {
      val emr = new AWS_EMR(jsonInput("params"))
      emr.run()
    }

    case "dataproc" => {
      println("Not Implemented Yet")
    }

    case unexpected => println("Invalid cloud_handler")
  }

}
