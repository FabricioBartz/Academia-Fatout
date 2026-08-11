package com.academia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling 
public class AcademiaApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(AcademiaApplication.class, args);
        var env = ctx.getEnvironment();
        String port = env.getProperty("local.server.port", env.getProperty("server.port", "8080"));
        System.out.println("✅ Academia FatOut está rodando em: http://localhost:" + port);
        // Banco configurado via application.properties (MySQL)
    }
}