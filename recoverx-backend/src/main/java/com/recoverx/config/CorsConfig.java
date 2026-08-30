package com.recoverx.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The bundled frontend (src/main/resources/static/index.html) is served from
 * the same origin as the API, so CORS isn't strictly required for it. This is
 * kept permissive so the frontend can also be run standalone (e.g. opened
 * from a different port during development) against this backend.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
