package com.swe573.socialhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class})
public class SocialHubApplication {

    public static void main(String... args) {
        SpringApplication.run(SocialHubApplication.class, args);
    }
}