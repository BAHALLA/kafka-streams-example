# Kafka streams examples
## How to build
1- Run kafka using `docker-compose.yaml`
```shell
docker compose up -d
```

2- Create topics 
```shell
kafka-topics.sh --bootstrap-server localhost:9094 --topic word-count-input --create --partitions 2 --replication-factor 1
kafka-topics.sh --bootstrap-server localhost:9094 --topic word-count-output --create --partitions 2 --replication-factor 1
```

3- Run application
```shell
mvn exec:java -Dexec.mainClass="org.example.WordsCountApp"
```

5- Produce messages from command line
```shell
kafka-console-producer.sh --broker-list localhost:9094 \
                          --topic word-count-input
```

4- Start consumer from command line
```shell
kafka-console-consumer.sh  --bootstrap-server localhost:9094 \
                           --topic word-count-output \
                           --from-beginning \
                           --formatter kafka.tools.DefaultMessageFormatter \
                           --property print.key=true \
                           --property print.value=true \
                           --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
                           --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
```

