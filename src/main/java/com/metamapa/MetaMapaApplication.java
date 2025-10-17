package com.metamapa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.metamapa.domain")
@EnableJpaRepositories(basePackages = "com.metamapa.repository")
public class MetaMapaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetaMapaApplication.class, args);
        System.out.println("✓ MetaMapa Application iniciada correctamente");
    }
}

