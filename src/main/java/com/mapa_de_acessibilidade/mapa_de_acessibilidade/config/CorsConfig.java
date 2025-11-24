package com.mapa_de_acessibilidade.mapa_de_acessibilidade.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplica a configuração a todos os endpoints
                .allowedOrigins("*") // Permite qualquer origem (para desenvolvimento)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Permite os métodos que você usa
                .allowedHeaders("*"); // Permite todos os cabeçalhos
    }
}