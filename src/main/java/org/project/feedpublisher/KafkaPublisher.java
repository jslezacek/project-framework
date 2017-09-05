package org.project.feedpublisher;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaPublisher {

    Producer producer;

    public KafkaPublisher(String bootstrapServer) {
        Properties config = new Properties();
        config.put("bootstrap.servers",bootstrapServer);
        config.put("acks","all");
        config.put("retries",1);
        //config.put("batch.size",32384);
        config.put("linger.ms",1);
        //config.put("buffer.memory",33554432);
        config.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        this.producer = new KafkaProducer<String, String>(config);
    }

    public void send(String topic, String message) {
        System.out.println("Send message to kafka topic: " + topic);
        this.producer.send(new ProducerRecord(topic, message));
        this.producer.flush();
    }
}
