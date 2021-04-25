package com.example.demo.aws.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Testcontainers
class AwsSqsTestDemoApplicationTests {

  @Container
  static final LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
      .withServices(LocalStackContainer.Service.SQS);

  @Autowired
  AmazonSQSAsync amazonSQSAsync;

  @Autowired
  BlockingQueue<Message<String>> messages;

  @DynamicPropertySource
  static void setup(DynamicPropertyRegistry registry) {
    AmazonSQS amazonSQS = AmazonSQSClientBuilder.standard()
        .withEndpointConfiguration(localstack.getEndpointConfiguration(LocalStackContainer.Service.SQS))
        .withCredentials(localstack.getDefaultCredentialsProvider())
        .build();
    amazonSQS.createQueue("demoQueue");
    registry.add("cloud.aws.credentials.access-key", localstack::getAccessKey);
    registry.add("cloud.aws.credentials.secret-key", localstack::getSecretKey);
    registry.add("cloud.aws.region.static", localstack::getRegion);
    registry.add("cloud.aws.sqs.endpoint", localstack.getEndpointConfiguration(LocalStackContainer.Service.SQS)::getServiceEndpoint);
  }

  @Test
  void contextLoads() throws InterruptedException {
    QueueMessagingTemplate template = new QueueMessagingTemplate(amazonSQSAsync);
    template.send("demoQueue", MessageBuilder.withPayload("Hello World!").build());

    Message<String> message = messages.poll(10, TimeUnit.SECONDS);
    Assertions.assertThat(message.getPayload()).isEqualTo("Hello World!");
  }

}
