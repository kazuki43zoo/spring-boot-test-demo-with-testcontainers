package com.example.demo.kafka;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Testcontainers
class KafkaTestDemoApplicationTests {

  @Container
  static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));

  @Autowired
  KafkaOperations<?, ?> kafkaOperations;

  @Autowired
  BlockingQueue<Message<String>> messages;

  @DynamicPropertySource
  static void setup(DynamicPropertyRegistry registry) {
    registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
  }

  @Test
  void contextLoads() throws InterruptedException {
    Assertions.assertThat(kafka.isRunning()).isTrue();
    kafkaOperations.send(MessageBuilder.withPayload("Hello!").build());

    Message<String> message = messages.poll(10, TimeUnit.SECONDS);
    Assertions.assertThat(message.getPayload()).isEqualTo("Hello!");
  }

}
