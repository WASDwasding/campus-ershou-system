package com.campus.ershou;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@MapperScan("com.campus.ershou.**.mapper")
public class CampusErshouApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusErshouApplication.class, args);
    }
}
