package com.example.demo.gcp.pubsub;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.PubSubOperations;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PubSubEmulatorContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Testcontainers
class GcpPubsubTestDemoApplicationTests {

  @Container
  static final PubSubEmulatorContainer pubsub
      = new PubSubEmulatorContainer(DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk:316.0.0-emulators"));

  @Autowired
  PubSubOperations pubSubOperations;

  @Autowired
  BlockingQueue<String> messages;

  @DynamicPropertySource
  static void setup(DynamicPropertyRegistry registry) {
    registry.add("spring.cloud.gcp.pubsub.emulator-host", pubsub::getEmulatorEndpoint);
  }

  @BeforeAll
  static void setup() throws Exception {
    ManagedChannel channel =
        ManagedChannelBuilder.forTarget("dns:///" + pubsub.getEmulatorEndpoint())
            .usePlaintext()
            .build();
    TransportChannelProvider channelProvider =
        FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));

    TopicAdminClient topicAdminClient =
        TopicAdminClient.create(
            TopicAdminSettings.newBuilder()
                .setCredentialsProvider(NoCredentialsProvider.create())
                .setTransportChannelProvider(channelProvider)
                .build());

    SubscriptionAdminClient subscriptionAdminClient =
        SubscriptionAdminClient.create(
            SubscriptionAdminSettings.newBuilder()
                .setTransportChannelProvider(channelProvider)
                .setCredentialsProvider(NoCredentialsProvider.create())
                .build());

    PubSubAdmin admin =
        new PubSubAdmin(() -> "gcp-test", topicAdminClient, subscriptionAdminClient);

    admin.createTopic("demoTopic");
    admin.createSubscription("demoTopic-sub", "demoTopic");

    admin.close();
    channel.shutdown();
  }

  @Test
  void contextLoads() throws InterruptedException {
    pubSubOperations.publish("demoTopic", "Hello World!");
    Assertions.assertThat(messages.poll(10, TimeUnit.SECONDS)).isEqualTo("Hello World!");
  }

}
