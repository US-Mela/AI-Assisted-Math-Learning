package com.hcmus.mela;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MelaApplication {
    public static void main(String[] args) {
        SpringApplication.run(MelaApplication.class, args);
    }
}
