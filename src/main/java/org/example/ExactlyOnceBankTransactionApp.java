package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.time.Instant;
import java.util.Properties;

public class ExactlyOnceBankTransactionApp {
    public static void main(String[] args) {
        final String bootstrapServers = args.length > 0 ? args[0] : "localhost:9094";

        final Properties streamsConfiguration = getStreamsConfiguration(bootstrapServers);

        final JsonSerializer<JsonNode> serializer = new JsonSerializer<>();
        final JsonDeserializer<JsonNode> deserializer = new JsonDeserializer<>();
        final Serde<JsonNode> jsonNodeSerde = Serdes.serdeFrom(serializer, deserializer);

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, JsonNode> stream = builder.stream("bank-transactions", Consumed.with(Serdes.String(), jsonNodeSerde));

        ObjectNode initialBalance = JsonNodeFactory.instance.objectNode();
        initialBalance.put("count", 0);
        initialBalance.put("balance", 0);
        initialBalance.put("time", Instant.ofEpochMilli(0L).toString());

        KTable<String,JsonNode> table = stream
                .groupByKey()
                .aggregate(
                        () -> initialBalance,
                        (key, transaction, balance) -> newBalance(transaction,balance)
                );
        table.toStream().to( "bank-balance-exactly-once",  Produced.with(Serdes.String(), jsonNodeSerde));

        KafkaStreams transactionStream = new KafkaStreams(builder.build(), streamsConfiguration);
        transactionStream.cleanUp();
        transactionStream.start();
        Runtime.getRuntime().addShutdownHook(new Thread(transactionStream::close));

    }

    private static Properties getStreamsConfiguration(final String bootstrapServers) {
        final Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "exactly-once-bank-transaction-app");
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "exactly-once-bank-transaction-app");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        streamsConfiguration.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);

        return streamsConfiguration;
    }

    private static JsonNode newBalance(JsonNode transaction, JsonNode balance) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();

        node.put("count", balance.get("count").asInt() + 1);
        node.put("balance", balance.get("balance").asInt() + transaction.get("amount").asInt());

        Long balanceEpoch = Instant.parse(balance.get("time").asText()).toEpochMilli();
        Long transactionEpoch = Instant.parse(transaction.get("time").asText()).toEpochMilli();
        Instant newBalanceIntance = Instant.ofEpochMilli(Math.max(balanceEpoch, transactionEpoch));
        node.put("time", newBalanceIntance.toString());
        return node;
    }
}
