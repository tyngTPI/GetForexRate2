package com.example.getforexrate2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GetForexRate2Application {

    public static void main(String[] args) {
        SpringApplication.run(GetForexRate2Application.class, args);
    }

}
