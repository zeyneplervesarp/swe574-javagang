package com.swe573.socialhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class})
public class SocialHubApplication {

    public static final Date SITE_CREATION_DATE = new Date(1619868495782L);

    public static void main(String... args) {
        SpringApplication.run(SocialHubApplication.class, args);
    }
}