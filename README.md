# Kafka streams examples
## How to build
1- Run kafka using `docker-compose.yaml`
```shell
docker compose up -docker
```
2- Create topic 
```shell
kafka-topics.sh --bootstrap-server localhost:9094 --topic word-count-input --create --partitions 6 --replication-factor 1
```
3- Run application
```shell
mvn exec:java -Dexec.mainClass="org.example.App"
```