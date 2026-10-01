package com.forkdevs.driveos.platform.operations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OperationsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OperationsServiceApplication.class, args);
    }
}
