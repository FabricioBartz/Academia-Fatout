package com.academia.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.base-dir:${user.home}/academia/uploads}")
    private String uploadBaseDir;

    @PostConstruct
    public void ensureUploadDirs() {
        try {
            Path base = Paths.get(uploadBaseDir);
            Files.createDirectories(base);
            Files.createDirectories(base.resolve("perfis"));
        } catch (Exception ignored) {
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir /uploads/** a partir de diretório externo configurável
        String normalized = Paths.get(uploadBaseDir).toAbsolutePath().toString().replace("\\", "/");
        if (!normalized.endsWith("/")) normalized = normalized + "/";
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:" + normalized, "classpath:/static/uploads/");
    }
}
