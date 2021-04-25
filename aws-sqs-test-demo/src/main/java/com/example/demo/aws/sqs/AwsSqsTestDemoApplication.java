package com.example.demo.aws.sqs;

import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootApplication
public class AwsSqsTestDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(AwsSqsTestDemoApplication.class, args);
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

    @SqsListener("demoQueue")
    void consume(Message<String> message) {
      System.out.println(message);
      messages.offer(message);
    }

  }
}
