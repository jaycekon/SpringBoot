package com.jaycekon.mybatis.multi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootWebApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(SpringBootWebApplication.class);
        springApplication.setWebEnvironment(true);
        springApplication.run(args);
    }
}
