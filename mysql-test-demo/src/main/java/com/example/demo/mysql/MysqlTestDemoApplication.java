package com.example.demo.mysql;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MysqlTestDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(MysqlTestDemoApplication.class, args);
  }

  @Mapper
  interface MyMapper {
    @Insert("insert into sample (id, name) values(#{id} ,#{name})")
    void create(Sample sample);

    @Select("select * from sample where id = #{id}")
    Sample findOne(int id);
  }

  static class Sample {
    public int id;
    public String name;
  }

}
