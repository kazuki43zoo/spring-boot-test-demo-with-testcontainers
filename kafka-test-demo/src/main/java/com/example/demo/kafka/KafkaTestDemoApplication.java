package com.example.demo.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootApplication
public class KafkaTestDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(KafkaTestDemoApplication.class, args);
  }

  @Bean
  public NewTopic demoTopic() {
    return TopicBuilder.name("demoTopic").build();
  }

  @Bean
  public BlockingQueue<Message<String>> messages() {
    return new LinkedBlockingQueue<>();
  }

  @Component
  class MyConsumer {

    private final BlockingQueue<Message<String>> messages;

    MyConsumer(BlockingQueue<Message<String>> messages) {
      this.messages = messages;
    }

    @KafkaListener(topics = "demoTopic", groupId = "demoGroup")
    void consume(Message<String> message) {
      System.out.println(message);
      messages.offer(message);
    }

  }
}
