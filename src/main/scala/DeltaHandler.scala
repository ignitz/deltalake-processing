package deltaprocessing

import org.apache.log4j.Logger

import org.apache.spark.sql.{SparkSession, DataFrame, Row, AnalysisException}
import org.apache.spark.sql.functions._

import java.util.Base64
import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime}

import utils.DataFrameTools._

import io.delta.tables._

class DeltaHandler(
    tableName: String,
    primaryKey: String,
    sourcePath: String,
    sourceFormat: String,
    targetPath: String,
    partitionBy: String
) {
  val logger = Logger.getLogger(getClass().getName())

  val spark = SparkSession.builder
    .appName("replicator")
    .config("spark.master", "local[*]")
    .getOrCreate()

  /**
   * *************************************************************************
   * Data String format to use on dataprocessing directory.
   * Ex:
   * hdfs:///local/hudi/processing/run=YYYY-MM-dd-HH-MM-SS/
   * hdfs:///local/hudi/repartition/run=YYYY-MM-dd-HH-MM-SS/
   *
   * This is useful for DEBUG failed jobs
   * *************************************************************************
   */
  val pathCompatibleFormatter =
    DateTimeFormatter.ofPattern("YYYY-MM-dd-HH-MM-SS")
  val now = LocalDateTime.now
  val datetimeProcessing = pathCompatibleFormatter.format(now)
  // logger.info(s"Running time (datetimeProcessing) ${now}")

  // def createDataFrame(df: DataFrame):

  private def checkIfDeltaTableExists(): Boolean = {
    var datasetExist = false
    try {
      var dfDelta = spark.read
        .format("delta")
        .load(targetPath)
      datasetExist = true
      logger.info("DeltaLake's table found.")
    } catch {
      case e: org.apache.spark.sql.AnalysisException => {
        logger.info("No DeltaLake's table found. Try to create a new one.", e)
      }
      case e: Throwable => {
        throw e
      }
    }

    return datasetExist
  }

  private def createFullLoadDataset() {
    val df = spark.read
      .format(sourceFormat)
      .load(sourcePath)
      .drop("year", "month", "day", "hour")

    val dfDedupInsert = deduplicateRows(
      df = df.filter("op in ('r', 'c', 'u')").select("after.*", "ts_ms"),
      pkCol = primaryKey
    )
    val dfDedupDelete = deduplicateRows(
      df = df.filter("op = 'd'").select("before.*", "ts_ms"),
      pkCol = primaryKey
    )

    if (partitionBy != null) {
      dfDedupInsert
        .drop("ts_ms")
        .write
        .partitionBy(partitionBy)
        .format("delta")
        .mode("overwrite")
        .option("overwriteSchema", "true")
        .save(targetPath)
    } else {
      dfDedupInsert
        .drop("ts_ms")
        .write
        .format("delta")
        .mode("overwrite")
        .option("overwriteSchema", "true")
        .save(targetPath)
    }

    if (dfDedupDelete.filter("op = 'd'").count > 0) {
      // delete

      DeltaTable
        .forPath(sparkSession = spark, path = targetPath)
        .delete(
          col(primaryKey).isin(dfDedupDelete(primaryKey))
        )
    }

  }

  private def upsertDelete() {
    val df = spark.read
      .format(sourceFormat)
      .load(sourcePath)
      .drop("year", "month", "day", "hour")

    val dfDedupUpsert = deduplicateRows(
      df = df.filter("op in ('r', 'c', 'u')").select("after.*", "ts_ms"),
      pkCol = primaryKey
    )
    val dfDedupDelete = deduplicateRows(
      df = df.filter("op = 'd'").select("before.*", "ts_ms"),
      pkCol = primaryKey
    )

    val stringExp = if (partitionBy != null) {
      s"${tableName}.${primaryKey} = updates.${primaryKey} AND ${tableName}.${partitionBy} = updates.${partitionBy}"
    } else {
      s"${tableName}.${primaryKey} = updates.${primaryKey}"
    }

    DeltaTable
      .forPath(sparkSession = spark, path = targetPath)
      .as(tableName)
      .merge(
        dfDedupUpsert.as("updates"),
        stringExp
      )
      .whenMatched()
      .updateAll()
      .whenNotMatched()
      .insertAll()
      .execute()

    if (dfDedupDelete.filter("op = 'd'").count > 0) {
      DeltaTable
        .forPath(sparkSession = spark, path = targetPath)
        .as(tableName)
        .merge(
          dfDedupDelete.as("deletes"),
          s"${tableName}.${primaryKey} = deletes.${primaryKey}"
        )
        .whenMatched()
        .delete()
        .execute()
    }

  }

  def run(): Boolean = {
    if (checkIfDeltaTableExists()) {
      logger.info(s"Upsert table ${targetPath}")
      upsertDelete()
    } else {
      logger.info(s"Create table ${targetPath}")
      createFullLoadDataset()
    }

    return true
  }
}
