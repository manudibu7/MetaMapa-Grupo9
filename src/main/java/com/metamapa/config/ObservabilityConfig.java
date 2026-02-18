package com.metamapa.config;


import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ObservabilityConfig {

    // Este Bean es fundamental para que las trazas (Tracing) funcionen
    // cuando haces peticiones HTTP. Spring inyecta automáticamente
    // la configuración de OpenTelemetry aquí.
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    //nO SABEMOS QUE HAY QUE CAMBIAR
}
//    @Bean
//    public OpenTelemetryAppender otelLogAppender(OpenTelemetry openTelemetry) {
//        OpenTelemetryAppender appender = new OpenTelemetryAppender();
//        appender.install(openTelemetry); // Aquí es donde ocurre la magia de unión
//        return appender;
//    }
