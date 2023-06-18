package org.example;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class ExactlyOnceBankTransactionProducer {

    public static void main(String[] args) {

        Producer<String, String> producer = getProducerConfig();

        int i = 0;
         while (true) {
             System.out.println("Producing batch n°: " + i);
             try {
                 producer.send(newTransactionRecord("Taoufiq"));
                 Thread.sleep(100);
                 producer.send(newTransactionRecord("Ali"));
                 Thread.sleep(100);
                 producer.send(newTransactionRecord("Stephane"));
                 i++;
             }
             catch (InterruptedException e) {
                 break;
             }
         }
    }

    private static Producer<String, String> getProducerConfig() {
        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9094");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, "3");
        properties.put(ProducerConfig.LINGER_MS_CONFIG, "1");
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

        Producer<String, String> producer = new KafkaProducer<>(properties);
        return producer;
    }

    public static ProducerRecord<String, String> newTransactionRecord(String name) {
        ObjectNode transaction = JsonNodeFactory.instance.objectNode();

        transaction.put("name", name);
        transaction.put("amount", ThreadLocalRandom.current().nextInt(0, 100));
        transaction.put("time", Instant.now().toString());

        return new ProducerRecord<>("bank-transactions", name, transaction.toString());

    }
}
