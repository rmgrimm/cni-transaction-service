package com.redhat.consulting.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TransactionApplication {

    public static void main(String[] args) {
        new SpringApplication(TransactionApplication.class).run(args);
    }

}
