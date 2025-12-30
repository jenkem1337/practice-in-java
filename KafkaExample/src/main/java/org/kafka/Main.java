package org.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws IOException {

        Runnable producerRunnable = () -> {
            Properties producerConf = new Properties();
            try {
                producerConf.load(new FileReader("src/main/resources/producer.properties"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            KafkaProducer<String, String> producer = new KafkaProducer<>(producerConf);

            while(true) {
                try{
                    producer.send(new ProducerRecord<>("test-topic", String.valueOf(System.nanoTime()), "test-value " + UUID.randomUUID().toString()));
                    producer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Runnable consumerRunnable = () -> {
            Properties consumerConf = new Properties();
            try {
                consumerConf.load(new FileReader("src/main/resources/consumer.properties"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerConf);
            consumer.subscribe(Collections.singleton("test-topic"));

            while (true){
                try {
                    var records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, String> record : records) {
                        System.out.println(String.format("topic = %s, partition = %s, offset = %d, key = %s, value = %s",
                                record.topic(), record.partition(), record.offset(), record.key(), record.value()));
                    }
                } catch (Exception e) {
                    consumer.close();
                }
            }
        };

        Thread producerThread = new Thread(producerRunnable);
        Thread consumerThread = new Thread(consumerRunnable);
        producerThread.start();
        consumerThread.start();
    }
}