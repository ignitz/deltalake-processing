package utils

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

object DataFrameTools {
  def deduplicateRows(
      df: DataFrame,
      pkCol: String,
      sortColumn: String = "ts_ms",
      repartitionBy: String = null
  ): DataFrame = {
    return if (repartitionBy != null) {
      df
        .repartition(col(repartitionBy))
        .sort(col(sortColumn).desc)
        .dropDuplicates(pkCol)
    } else {
      df
        .coalesce(1)
        .sort(col(sortColumn).desc)
        .dropDuplicates(pkCol)
    }
  }
}
