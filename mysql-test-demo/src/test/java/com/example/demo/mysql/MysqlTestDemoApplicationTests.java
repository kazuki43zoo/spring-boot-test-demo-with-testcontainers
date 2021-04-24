package com.example.demo.mysql;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
class MysqlTestDemoApplicationTests {

  @Container
  private static final MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql"))
      .withUsername("devuser")
      .withPassword("devuser")
      .withDatabaseName("devdb");

  @Autowired
  MysqlTestDemoApplication.MyMapper mapper;

  @DynamicPropertySource
  static void setup(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", mysql::getJdbcUrl);
  }

  @Test
  void contextLoads() {
    Assertions.assertThat(mysql.isRunning()).isTrue();
    {
      MysqlTestDemoApplication.Sample sample = new MysqlTestDemoApplication.Sample();
      sample.id = 1;
      sample.name = "Test";
      mapper.create(sample);
    }
    {
      MysqlTestDemoApplication.Sample sample = mapper.findOne(1);
      Assertions.assertThat(sample.id).isEqualTo(1);
      Assertions.assertThat(sample.name).isEqualTo("Test");
    }
  }

}
