package com.hafidh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class HafidhApplication {
    public static void main(String[] args) {
        SpringApplication.run(HafidhApplication.class, args);
    }
}