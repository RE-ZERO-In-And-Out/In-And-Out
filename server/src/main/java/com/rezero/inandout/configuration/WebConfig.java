package com.rezero.inandout.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://3.34.206.181:3000", "http://localhost:3000")
            .allowedMethods("GET","POST","PUT","DELETE","OPTIONS","PATCH")
            .allowedHeaders("*")
            .maxAge(3000);
    }
}
