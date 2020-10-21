# deltalake-processing

Prototype for [DeltaLake](https://docs.delta.io/latest/delta-intro.html)'s cdc. Spark Kafka not implemented yet.

## Amazon EMR

`emr-6.1.0` tem Spark `3.0.0`

## Build with docker

Linux and macOS

```
docker volume create sbt_data
docker volume create sbt_ivy_data
docker run -it --rm -v sbt_ivy_data:/root/.ivy2 -v sbt_data:/root/.sbt -v $PWD:/app -w /app mozilla/sbt sbt assembly
```

## Shell

```shell
spark-shell --packages io.delta:delta-core_2.12:0.7.0 --conf "spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension" --conf "spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog"
```

## Run

```shell
spark-submit \
    --conf "spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension" \
    --conf "spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog" \
    --class deltaprocessing.Main \
    target/scala-2.12/deltalake-processing-assembly-1.0.jar \
    '''{
        "service": "emr",
        "params": {
            "configPath": "config/config_local.json"
        }
    }''')
```

# TODO

Pass Kafka Bootstrap Server to read direct from Kafka Broker

```scala
val lines = spark.readStream.format("kafka").option("kafka.bootstrap.servers", "XXXXXXXXXX").
            option("subscribe", "retail").
            load.select($"value".cast("string").alias("value"))
```

