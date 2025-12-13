package com.academia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AcademiaApplication {
    public static void main(String[] args) {
        SpringApplication.run(AcademiaApplication.class, args);
        System.out.println("✅ Academia FatOut está rodando em: http://localhost:8080");
        System.out.println("✅ H2 Console: http://localhost:8080/h2-console");
    }
}