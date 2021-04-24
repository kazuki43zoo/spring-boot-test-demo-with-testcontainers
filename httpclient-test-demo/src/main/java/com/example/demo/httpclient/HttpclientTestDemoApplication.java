package com.example.demo.httpclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class HttpclientTestDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(HttpclientTestDemoApplication.class, args);
  }

  @Component
  class MyClient {
    final WebClient client;

    MyClient(WebClient.Builder builder, @Value("${api.baseUrl:http://localhost:8080/}") String baseUrl) {
      this.client = builder.baseUrl(baseUrl).build();
    }

    String hello() {
      return client.get()
          .uri("/hello")
          .exchangeToMono(res -> res.bodyToMono(String.class))
          .block();
    }


  }
}
