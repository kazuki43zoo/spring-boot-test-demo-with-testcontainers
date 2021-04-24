package com.example.demo.httpclient;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
class HttpclientTestDemoApplicationTests {

  @Container
  static final MockServerContainer mockServer =
      new MockServerContainer(DockerImageName.parse("jamesdbloom/mockserver:mockserver-5.11.2"));

  MockServerClient mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());

  @DynamicPropertySource
  static void setup(DynamicPropertyRegistry registry) {
    registry.add("api.baseUrl", () -> "http://" + mockServer.getHost() + ":" + mockServer.getServerPort());
  }

  @Autowired
  HttpclientTestDemoApplication.MyClient myClient;

  @Test
  void contextLoads() {
    mockServerClient
        .when(HttpRequest.request().withPath("/hello"))
        .respond(HttpResponse.response().withBody("Hello!"));

    String message = myClient.hello();
    Assertions.assertThat(message).isEqualTo("Hello!");
  }


}
