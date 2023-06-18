package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;

import static  org.junit.Assert.*;

public class ExactlyOnceBankTransactionProducerTest {


    @Test
    public void newTransactionRecordTest() throws JsonProcessingException {
        ProducerRecord<String,String> transactionRecord = ExactlyOnceBankTransactionProducer.newTransactionRecord("Taoufiq");
        String key = transactionRecord.key();
        String value = transactionRecord.value();

        assertEquals(key, "Taoufiq");
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode node = objectMapper.readTree(value);
        assertEquals(node.get("name").asText(), "Taoufiq");
        assertTrue("The amount should be less than 100", node.get("amount").asInt() < 100);
    }
}