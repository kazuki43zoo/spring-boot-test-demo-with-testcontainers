package com.example.demo.postgresql;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
class PostgresqlTestDemoApplicationTests {

  @Container
  private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres"))
      .withUsername("devuser")
      .withPassword("devuser")
      .withDatabaseName("devdb");

  @Autowired
  PostgresqlTestDemoApplication.MyMapper mapper;

  @DynamicPropertySource
  static void setup(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
  }

  @Test
  void contextLoads() {
    Assertions.assertThat(postgres.isRunning()).isTrue();
    {
      PostgresqlTestDemoApplication.Sample sample = new PostgresqlTestDemoApplication.Sample();
      sample.id = 1;
      sample.name = "Test";
      mapper.create(sample);
    }
    {
      PostgresqlTestDemoApplication.Sample sample = mapper.findOne(1);
      Assertions.assertThat(sample.id).isEqualTo(1);
      Assertions.assertThat(sample.name).isEqualTo("Test");
    }
  }

}
