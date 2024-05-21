package com.prolegacy.atom2024backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableRetry
public class Atom2024BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(Atom2024BackendApplication.class, args);
    }
}
