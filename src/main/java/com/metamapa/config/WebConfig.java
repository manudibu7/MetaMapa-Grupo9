package com.metamapa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * config para servir archivos estaticos desde la carpeta uploads.
 * deja acceder a los archivos mediante URLs como: /uploads/holahola123.jpg
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // mapea la URL /uploads/** a la carpeta fisica uploads/ en la raiz del proyecto
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}

