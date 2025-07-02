package com.project.workboard.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // To Allow only the frontend origin
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        // Setting the HTTP methods the front-end can use
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // To Allow "Authorization" & "Content-Type" headers used in API communication
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // This is Needed since we'r sending JWT in headers or cookies
        config.setAllowCredentials(true);
        // To cache pre-flight response for 1 hour
        config.setMaxAge(3600L);
        // Apply to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
