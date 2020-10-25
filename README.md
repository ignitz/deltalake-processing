# deltalake-processing

Prototype for [DeltaLake](https://docs.delta.io/latest/delta-intro.html)'s cdc. Spark Kafka not implemented yet.

## Amazon EMR

`emr-6.1.0` tem Spark `3.0.0`

## Build with docker

Linux and macOS

Create volumes to cache sbt jars.
```
docker volume create sbt_data
docker volume create sbt_ivy_data
```

To compile using sbt assembly.

```
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

```shell
spark-submit  --conf "spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension"  --conf "spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog"  --class deltaprocessing.Main s3://333728661073-dev-configs/deltalake/deltalake-processing-assembly-1.0.jar "{ \"service\": \"emr\", \"params\": { \"configPath\": \"s3://333728661073-dev-configs/deltalake/config_local.json\" } }"
```

# TODO

Pass Kafka Bootstrap Server to read direct from Kafka Broker

```scala
val lines = spark.readStream.format("kafka").option("kafka.bootstrap.servers", "XXXXXXXXXX").
            option("subscribe", "retail").
            load.select($"value".cast("string").alias("value"))
```

# DATAFRAMES Example

```scala
scala> var df = spark.read.format("delta").load("fs/s3/333728661073-dev-kafka-staged/DATAFEEDER_register/dbo/persons")
df: org.apache.spark.sql.DataFrame = [id: int, cpf: string ... 6 more fields]

scala> df.show()
+---+--------------+-----------------+--------------------+------+----------+--------------------+--------------------+
| id|           cpf|             name|               email|gender|birth_date|          created_at|          updated_at|
+---+--------------+-----------------+--------------------+------+----------+--------------------+--------------------+
|  2|667.261.873-27| Michele Hartmann| michele46@gmail.com|     F|       -19|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  3|596.936.288-37|     Edvige Sanna|edvigesanna08@yah...|     F|      6196|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  5|250.164.184-11|     Nadine Ebert|nadineebert77@yah...|     F|      5470|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  9|319.672.749-16|   Lillian Grimes|lilliangrimes89@y...|     F|     -4767|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 11|348.383.909-71|   Darlene Kuphal|darlene.kuphal@ho...|     F|      1126|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 14|642.425.123-50| Marjorie Leffler|marjorie.leffler5...|     F|      9500|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 15|735.136.595-47|       Kari Moore|kari.moore@hotmai...|     F|       424|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 16|749.192.983-40|     Shari Cassin|   shari81@yahoo.com|     F|      6583|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 22|051.210.272-49|   Kathryn Russel|kathryn.russel50@...|     F|      9392|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 23|463.453.551-35|       Heidi Haag|heidi.haag@gmail.com|     F|      -347|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 25|378.143.660-89|       Myra Dicki|myra.dicki@yahoo.com|     F|     15087|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 26|293.643.879-96| Serafin Morawski|serafinmorawski01...|     F|     -3703|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 27|309.132.751-58|Violet Swaniawski|violetswaniawski6...|     F|     -2617|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 28|339.263.688-61|Виктория Логинова|         .@yandex.ru|     F|    -12688|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 29|783.292.242-79|      Melba Boehm| melba22@hotmail.com|     F|      5265|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 30|578.763.724-06|    Karla Smitham|   karla87@gmail.com|     F|    -16035|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 31|500.446.089-26|        Jan Thiel| jan.thiel@yahoo.com|     F|     -6132|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 32|201.825.213-54|  Heather Trantow| heather84@gmail.com|     F|      7096|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 33|855.045.232-73|    Dianna Walter|diannawalter48@ho...|     F|    -12763|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 34|209.458.684-72| Gwendolyn Zemlak|gwendolynzemlak20...|     F|      3993|2020-10-18T23:44:...|2020-10-18T23:44:...|
+---+--------------+-----------------+--------------------+------+----------+--------------------+--------------------+
only showing top 20 rows


scala> var df = spark.read.format("delta").load("fs/s3/333728661073-dev-kafka-staged/DATAFEEDER_register/dbo/addresses")
df: org.apache.spark.sql.DataFrame = [id: int, person_id: int ... 10 more fields]

scala> df.show()
+---+---------+--------------+------------+--------------+--------------------+--------------------+----------+-------------------+-------------------+--------------------+--------------------+
| id|person_id|       country|country_code|         state|                city|              street|   zipcode|           latitude|          longitude|          created_at|          updated_at|
+---+---------+--------------+------------+--------------+--------------------+--------------------+----------+-------------------+-------------------+--------------------+--------------------+
|  1|        1|      Bulgaria|          BG|  Pennsylvania|   Lake Marshallstad|38065 Genevieve S...|73953-9772|  50.29929999999999|          -155.6189|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  2|        2|        Poland|          PL|  Pennsylvania|      South Saraview|2661 Bartell Gard...|83112-5447|           -81.8099|           141.1064|2020-10-18T23:44:...|2020-10-19T01:29:...|
|  3|        3|      Barbados|          BB|        Nevada|       New Beckyfort|  63528 Marcia Views|92747-0622|-12.045699999999997| 103.20159999999998|2020-10-18T23:44:...|2020-10-19T00:12:...|
|  4|       11|  Cook Islands|          CK|     Minnesota|South Elizabethhaven| 9723 Beatty Islands|12584-8995|           -73.2751|            91.1454|2020-10-18T23:44:...|2020-10-19T02:51:...|
|  5|        4| Guinea-Bissau|          GW|  South Dakota|      West Luciatown|7987 Willis Causeway|     26230| 14.977599999999995|          -126.7027|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  6|       12|        Cyprus|          CY| Massachusetts|        East Richard|  6125 Clifton Forge|83699-2040|-23.976699999999994|            83.5102|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  7|        5|Norfolk Island|          NF|       Wyoming|  West Reginaldville|651 Alton Drives ...|66251-3600| 41.047799999999995|  49.37610000000001|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  8|       13|          Cuba|          CU|North Carolina|        Okunevaville|42708 Alonzo Stra...|90008-1370|           -41.6254|            -6.3322|2020-10-18T23:44:...|2020-10-19T00:54:...|
|  9|        6|     Nicaragua|          NI|      Delaware|      South Bobville|   4068 Jerde Cliffs|     33929|  53.93969999999999| 123.76679999999999|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 10|       14|       Belarus|          BY|      Colorado|         Manuelville|7898 Corkery Curv...|71026-9424|           -81.1234|          -114.4959|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 11|        7|  Vatican City|          VA|       Georgia|     Lake Judithland|   97595 Gladys Pass|68655-5090|  80.39940000000001|           129.4787|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 12|       15|          Mali|          ML|     Louisiana|   Lake Faithchester|   2122 Yvette Track|49922-1772|            72.9728|-0.9938999999999965|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 13|        8|       Belgium|          BE| West Virginia|        Donnachester|  91264 Randal Track|34398-0227|           -80.6508|          -179.6218|2020-10-18T23:44:...|2020-10-19T00:10:...|
| 14|       16|  Burkina Faso|          BF|         Maine|       Johnathanfort|5520 Maxine Place...|08353-0164|  66.27019999999999|           175.4144|2020-10-18T23:44:...|2020-10-19T01:56:...|
| 15|       21|      Kiribati|          KI|      Illinois|     North Pattiview|297 Amber Terrace...|78676-3353|  85.55439999999999|           -86.9382|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 16|        9|        Rwanda|          RW|    California|       Hermistonstad|184 Cecilia Crossing|83807-5386|           -47.5533|-12.625799999999998|2020-10-18T23:44:...|2020-10-19T00:38:...|
| 17|       10|   South Sudan|          SS|         Texas|          Lake Randy|9981 Rudy Shore S...|     05371| -67.55199999999999| 127.48180000000002|2020-10-18T23:44:...|2020-10-18T23:46:...|
| 18|       22|          Niue|          NU|       Wyoming|         Deannashire|73035 Rice Spring...|12659-4491| 46.598600000000005|          -154.8302|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 19|       17|Norfolk Island|          NF|         Idaho|       South Kenneth|   84920 Vickie Isle|     35547| 63.208200000000005|  31.90549999999999|2020-10-18T23:44:...|2020-10-18T23:50:...|
| 20|       18|        Jordan|          JO|       Vermont|        Phyllismouth|   126 Trantow Ranch|78766-0816| 43.514399999999995|           -43.5837|2020-10-18T23:44:...|2020-10-18T23:44:...|
+---+---------+--------------+------------+--------------+--------------------+--------------------+----------+-------------------+-------------------+--------------------+--------------------+
only showing top 20 rows


scala> var df = spark.read.format("delta").load("fs/s3/333728661073-dev-kafka-staged/DATAFEEDER_register/dbo/companies")
df: org.apache.spark.sql.DataFrame = [id: int, name: string ... 5 more fields]

scala> df.show()
+---+--------------------+--------------------+--------------------+--------------------+--------------------+--------------------+
| id|                name|               email|               phone|             website|          created_at|          updated_at|
+---+--------------------+--------------------+--------------------+--------------------+--------------------+--------------------+
|  1|     Heathcote Group|heathcote-groupke...|(551) 025-8978 x3...|https://www.annie...|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  2| Koepp-Volkman Group|koepp-volkman-gro...|      (315) 908-2057|http://www.danny.org|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  3|Kulas, Koch and T...|kulas-koch-and-tr...| 029.631.4894 x03368| https://bernard.com|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  4|Leuschke-Balistre...|leuschke-balistre...|      (764) 422-8307|https://www.darri...|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  5|    Balistreri Group|balistreri-group....| 395.973.0036 x31840|http://www.johnho...|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  6|Stiedemann-Jerde ...|stiedemann-jerde-...|   462-541-9179 x488|http://julie-bedn...|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  7|Graham, Frami and...|graham-frami-and-...|      1-499-393-6857|https://www.jodyl...|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  8|      Franecki Group|franecki-group58@...|      (310) 852-0564|http://www.tasha.com|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  9|Torphy-VonRueden ...|torphy-vonrueden-...|1-692-281-8926 x6994|https://traci-wat...|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 10|             Von LLC|von-llc.beahan65@...|      (404) 734-0774|https://www.alvin...|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 11|  Turner-Wehner Inc.|turner-wehner-inc...| 1-740-720-3079 x259|  http://courtney.eu|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 12|        Nicolas Inc.|nicolas-inc.mcder...|        981-526-1103|http://www.gordon.co|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 13|  Runte-Glover Group|runte-glover-grou...|   995-553-9222 x064|http://joannewyma...|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 14|    Satterfield Inc.|satterfield-inc.....|      (849) 186-6487|http://rosaliekau...|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 15|Kulas, Gorczany a...|kulas-gorczany-an...|        794.420.9513|https://ramonahil...|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 16| Bernier-Rolfson LLC|bernier-rolfson-l...|      1-383-360-5030|http://www.rosewu...|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 17|       Dickens Corp.|dickens-corp..emm...|        771.725.3042|https://www.ericm...|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 18|Dietrich, Watsica...|dietrich-watsica-...|1-220-157-9940 x7...| https://www.jan.org|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 19|  Rohan-Sanford Ltd.|rohan-sanford-ltd...| 769.280.3142 x75778|   http://craig.info|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 20|Jaskolski-Ondrick...|jaskolski-ondrick...|   789.784.0423 x145|https://www.krist...|2020-10-18T23:44:...|2020-10-18T23:44:...|
+---+--------------------+--------------------+--------------------+--------------------+--------------------+--------------------+
only showing top 20 rows


scala> var df = spark.read.format("delta").load("fs/s3/333728661073-dev-kafka-staged/DATAFEEDER_register/dbo/persons_companies")
df: org.apache.spark.sql.DataFrame = [id: int, person_id: int ... 3 more fields]

scala> df.show()
+---+---------+----------+--------------------+--------------------+
| id|person_id|company_id|          created_at|          updated_at|
+---+---------+----------+--------------------+--------------------+
|  1|        1|         1|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  2|        2|         2|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  3|        3|         3|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  4|        4|         4|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  5|        4|         5|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  6|        5|         6|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  7|        5|         7|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  8|       13|         8|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  9|        6|         9|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 10|       14|        10|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 11|       15|        11|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 12|       15|        12|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 13|        8|        13|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 14|       16|        14|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 15|        9|        15|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 16|        9|        16|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 17|       22|        18|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 18|       22|        17|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 19|       22|        19|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 20|       18|        20|2020-10-18T23:44:...|2020-10-18T23:44:...|
+---+---------+----------+--------------------+--------------------+
only showing top 20 rows


scala> var df = spark.read.format("delta").load("fs/s3/333728661073-dev-kafka-staged/DATAFEEDER_tracker/public/posts")
df: org.apache.spark.sql.DataFrame = [id: int, user_id: int ... 7 more fields]

scala> df.show()
+---+-------+--------------------+--------------------+--------------------+--------------------+-----------------+--------------------+--------------------+
| id|user_id|               title|            keywords|             content|                  ip|              mac|          created_at|          updated_at|
+---+-------+--------------------+--------------------+--------------------+--------------------+-----------------+--------------------+--------------------+
|  1|      1|Cupiditate ipsa n...|exercitationem,de...|Ut esse voluptati...|77ad:2e3b:32e3:a6...|1a:e1:10:a7:62:18|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  2|      1|Odit ea aut est c...|       qui,fugiat,in|Nobis praesentium...|      195.61.184.135|14:95:c3:16:5b:42|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  3|      1|A architecto nece...|voluptatem,nostru...|Nam rem possimus....|      38.116.159.104|5b:c4:70:69:fd:07|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  4|      1|Ipsa quis non qui...| consequatur,quia,et|Temporibus dolore...|f9e9:3883:711f:8d...|ed:34:2d:35:8f:f5|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  5|      1|Vel saepe eum cup...| sunt,accusantium,id|Consequatur qui s...|e01d:56f0:3b75:48...|69:b3:e8:96:a2:38|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  6|      1|Aliquid laborum e...| nulla,voluptate,hic|Numquam doloribus...|580c:23f5:2a17:c2...|32:f1:c5:37:e3:03|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  7|      1|Nulla aut asperna...|iusto,voluptatem,...|Ut natus non qui ...|3794:9085:0dec:78...|e3:47:1a:34:f9:1d|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  8|      1|Illum odit eum ex...|  deleniti,sed,dicta|Temporibus est re...|      61.138.201.184|26:db:a6:34:e5:e9|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  9|      1|Eos delectus maio...|  asperiores,est,rem|Sunt et inventore...|4c18:3bb4:146c:e6...|7d:af:62:a3:43:e3|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 10|      1|Enim alias aut la...|     eos,et,incidunt|Autem dolor quibu...|db7b:9552:073c:26...|3c:88:bd:02:3e:54|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 11|      1|In doloribus reru...|eligendi,corporis...|Ut ea eaque amet....|         13.40.196.8|e4:dd:67:f0:ad:90|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 12|      1|Esse pariatur et ...|   omnis,et,corrupti|Amet quo ipsa atq...|ce10:f07e:a5da:fc...|1a:1a:ec:bd:15:9f|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 13|      1|Nihil dicta quisq...|non,blanditiis,culpa|Et et quis vel. S...|ce23:1213:0b65:8e...|aa:39:86:82:97:5e|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 14|      1|Distinctio facere...|omnis,distinctio,...|Illum neque et re...|37dc:c389:1e28:29...|76:f3:7b:33:2d:e2|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 15|      1|      Nemo ullam ab.|omnis,nihil,tempo...|Mollitia voluptat...|9492:756e:d82c:ab...|45:8e:b4:87:4b:e9|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 16|      1|Impedit quo alias...|    illum,quia,ullam|Dignissimos archi...|4a3e:c5dd:dff5:78...|a8:b6:4a:3f:f0:40|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 17|      1|Perspiciatis quia...|dolores,molestiae...|Doloribus delectu...|eac4:1409:bc46:3b...|23:b0:ad:f8:29:8e|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 18|      1|Voluptatem aspern...|   officia,quia,iure|Maiores debitis n...|d39f:5cb0:1331:3a...|52:75:31:be:78:fa|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 19|      2|Assumenda quis as...|    nostrum,ut,ipsum|Est repudiandae i...|cdc5:4e6e:d753:7a...|ce:ae:51:0f:0d:b7|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 20|      2|Reprehenderit rep...|neque,necessitati...|Minima odio perfe...|      147.223.125.57|8c:2f:af:54:24:78|2020-10-18T23:44:...|2020-10-18T23:44:...|
+---+-------+--------------------+--------------------+--------------------+--------------------+-----------------+--------------------+--------------------+
only showing top 20 rows


scala> var df = spark.read.format("delta").load("fs/s3/333728661073-dev-kafka-staged/DATAFEEDER_tracker/public/users")
df: org.apache.spark.sql.DataFrame = [id: int, cpf: string ... 6 more fields]

scala> df.show()
+---+-----------+--------------------+--------------------+--------------------+--------------------+--------------------+--------------------+
| id|        cpf|               email|            username|            password|              avatar|          created_at|          updated_at|
+---+-----------+--------------------+--------------------+--------------------+--------------------+--------------------+--------------------+
|  1|23456480660|lonnie.mraz36@gma...|     lonnie.mraz9088|$2b$10$MxuprwEv3j...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-19T03:51:...|
|  2|66726187327| michele46@gmail.com|           michele78|$2b$10$fHsAdaz2k0...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  3|59693628837|edvigesanna08@yah...|    edvige.sanna2471|$2b$10$ACPG.vmzHM...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-18T23:50:...|
|  4|88518722608|emilio.hermann79@...|      emilio_hermann|$2b$10$OeWILo2QAi...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  5|25016418411|nadineebert77@yah...|        nadine_ebert|$2b$10$kZ4As./jdM...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  6|27125706347|lancecruickshank9...| lance.cruickshank35|$2b$10$5I7h2bK/Ix...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  7|88634867862|karlsmitham84@hot...|        karl.smitham|$2b$10$ijqKY1IvgO...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-18T23:44:...|
|  8|66733393492|teodoz.petr94@gma...|       teodoz.petr20|$2b$10$jiJ7N8Ne9h...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-18T23:47:...|
|  9|31967274916|lilliangrimes89@y...|    lillian.grimes87|$2b$10$kau5YJMHUw...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 10|70390182680|mike.steuber@gmai...|      mike_steuber91|$2b$10$vOxZEnpIiJ...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 11|34838390971|darlene.kuphal@ho...|     darlenekuphal81|$2b$10$CsCNtwNll1...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 12|70574380795|dominic.schinner@...|    dominic_schinner|$2b$10$NMCOoGJX0i...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-19T02:32:...|
| 13|54612559576|benniejohns86@hot...|      bennie.johns82|$2b$10$BFiNmEjqon...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 14|64242512350|marjorie.leffler5...|  marjorie.leffler04|$2b$10$Nm51i9roRW...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-19T02:27:...|
| 15|73513659547|kari.moore@hotmai...|        kari_moore39|$2b$10$5ID77tLth9...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-19T02:46:...|
| 16|74919298340|   shari81@yahoo.com|    shari.cassin9630|$2b$10$9rLkMY65vi...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 17|31080553568|cary.stokes18@gma...|              cary97|$2b$10$ouXI/xGoJ2...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-18T23:44:...|
| 18|16457453090|marion.ryan49@hot...|       marion.ryan20|$2b$10$XLjoasRw1q...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-19T00:23:...|
| 19|95569314148|austinrau66@hotma...|          austin_rau|$2b$10$asjBlw/a4C...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-19T02:29:...|
| 20|76975492042| shannon55@yahoo.com|shannon.weissnat0495|$2b$10$11Sodj.rJZ...|https://s3.amazon...|2020-10-18T23:44:...|2020-10-18T23:44:...|
+---+-----------+--------------------+--------------------+--------------------+--------------------+--------------------+--------------------+
only showing top 20 rows

```

