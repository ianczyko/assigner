package com.anczykowski.assigner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AssignerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssignerApplication.class, args);
    }

}
