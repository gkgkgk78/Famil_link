package com.famillink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import nu.pattern.OpenCV;
@SpringBootApplication
public class FamilLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(FamilLinkApplication.class, args);
        OpenCV.loadShared();
    }

}
