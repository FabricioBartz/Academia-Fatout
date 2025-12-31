package com.academia.config;

import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Value("${app.upload.base-dir:${user.home}/academia/uploads}")
    private String uploadBaseDir;

    @PostConstruct
    public void ensureUploadDirs() {
        try {
            Path base = Paths.get(uploadBaseDir);
            Files.createDirectories(base);
            Files.createDirectories(base.resolve("perfis"));
            logger.info("[Academia FatOut] Diretório de uploads configurado em: {}", base.toAbsolutePath());
            logger.info("[Academia FatOut] Diretório de fotos de perfil: {}", base.resolve("perfis").toAbsolutePath());
        } catch (Exception e) {
            logger.error("[Academia FatOut] Erro ao criar diretórios de upload: {}", e.getMessage());
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
