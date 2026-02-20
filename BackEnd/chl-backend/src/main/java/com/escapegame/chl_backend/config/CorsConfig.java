package com.escapegame.chl_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 1. Permitir el origen de tu Frontend Vue.js
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        
        // 2. Permitir métodos HTTP necesarios para la API REST
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 3. Permitir cabeceras estándar y de autorización (JWT)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        
        // 4. Exponer cabeceras si es necesario (útil para depuración)
        configuration.setExposedHeaders(List.of("Authorization"));
        
        // 5. Permitir credenciales (cookies o headers de autenticación)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuración a todas las rutas de la API
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}