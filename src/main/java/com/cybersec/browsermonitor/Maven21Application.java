package com.cybersec.browsermonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Maven21Application {

    public static void main(String[] args) {
        SpringApplication.run(Maven21Application.class, args);
        System.out.println("🚀 Browser Monitor Started on http://localhost:8080");
    }

}
