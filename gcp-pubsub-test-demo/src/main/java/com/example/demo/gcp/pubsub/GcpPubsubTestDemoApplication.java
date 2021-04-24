package com.example.demo.gcp.pubsub;

import com.google.cloud.spring.pubsub.core.PubSubOperations;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootApplication
public class GcpPubsubTestDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(GcpPubsubTestDemoApplication.class, args);
  }

  @Bean
  TaskScheduler pubsubSubscriberThreadPool() {
    return new ThreadPoolTaskScheduler();
  }

  @Bean
  BlockingQueue<String> messages() {
    return new LinkedBlockingQueue<>();
  }

  @Bean
  ApplicationRunner runner(PubSubOperations pubSubOperations, BlockingQueue<String> messages) {
    return args ->
        pubSubOperations.subscribe("demoTopic-sub", message -> {
          messages.offer(message.getPubsubMessage().getData().toStringUtf8());
          message.ack();
        });
  }

}
