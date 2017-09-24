package org.project.network;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaPublisher {
    String topic;
    Producer producer;

    public KafkaPublisher(String bootstrapServer, String topic) {
        this.topic = topic;
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

    public void send(String message) {
        this.producer.send(new ProducerRecord(this.topic, message));
    }

    public void flush() {
        this.producer.flush();
    }
}
