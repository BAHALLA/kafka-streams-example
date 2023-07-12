
# Schema registry examples

## Create new schema 

```shell
curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" --data \
 '{"schema": "{\"type\":\"record\",\"name\":\"Payment\",\"namespace\":\"my.examples\",\"fields\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"amount\",\"type\":\"double\"}]}"}' \
  http://localhost:8081/subjects/my-kafka-value/versions
```

## List all subjects
```shell
curl -X GET http://localhost:8081/subjects
```

## Update compatibility for a schema
```shell
curl -X PUT -H "Content-Type: application/vnd.schemaregistry.v1+json" \ 
 --data '{"compatibility": "FULL"}' http://localhost:8081/config/my-kafka-value
 
 curl -X GET http://localhost:8081/config/my-kafka-value

```

## Get schema types registered registry
```shell
curl -X GET http://localhost:8081/schemas/types
```