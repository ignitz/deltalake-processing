#!/bin/bash

time (sbt assembly && spark-submit \
    --conf "spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension" \
    --conf "spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog" \
    --class deltaprocessing.Main \
    target/scala-2.12/deltalake-processing-assembly-1.0.jar \
    '''{
        "service": "emr",
        "params": {
            "configPath": "s3://333728661073-dev-configs/deltalake/config_local.json"
        }
    }''')
