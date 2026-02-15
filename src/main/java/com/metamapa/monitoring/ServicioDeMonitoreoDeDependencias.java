package com.metamapa.monitoring;

import com.metamapa.monitoring.healthindicators.DatabaseHealthIndicator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ServicioDeMonitoreoDeDependencias {
    //Contadores y Maximos para alertas
    private int fallasCriticos = 0;

    @Value("${monitoreo.max-fallos:3}")
    private int MAX_FALLOS;

    @Value("${monitoreo.force-exit-on-critical:true}")
    private boolean forceExitOnCritical;

    //HealthIndicator para cada dependencia
    private final DatabaseHealthIndicator database;


    public ServicioDeMonitoreoDeDependencias(DatabaseHealthIndicator database
    ) {
        this.database = database;
    }

    private void manejarFallaCritica() {
        fallasCriticos++;
        if(fallasCriticos >= MAX_FALLOS) {
            log.error("Falla crítica persistente -> forzando restart (forceExitOnCritical={})", forceExitOnCritical);
            if (forceExitOnCritical) {
                // Lanzamos una excepción no controlada para que la plataforma pueda reiniciar el pod/servicio
                throw new IllegalStateException("AutoRestart por fallas críticas persistentes");
            } else {
                log.error("Se alcanzó el máximo de fallas críticas ({}), pero forceExitOnCritical=false. Revisar manualmente.", MAX_FALLOS);
            }
        } else {
            log.warn("Falla crítica detectada en dependencias esenciales. Se han registrado {} fallas consecutivas. Se recomienda revisar las dependencias críticas.", fallasCriticos);
        }
    }

    @Scheduled(fixedDelay = 15000)
    public void heartbeat() {
        try {
            boolean databaseOk = database.estaDisponible();


            log.debug("Estado dependencias - database: {}",
                    databaseOk);

            if (!databaseOk) {
                log.error("Dependencia crítica 'database' DOWN -> marcando y manejando");
                database.markDown();
            } else {
                database.markUp();
            }
        } catch (Exception ex) {
            // Capturamos cualquier excepción para que quede en logs y sea visible para Render
            log.error("Excepción en heartbeat de monitoreo: {}", ex.getMessage(), ex);
            // Re-lanzamos si forceExitOnCritical para que la plataforma reinicie
            if (forceExitOnCritical) {
                throw ex;
            }
        }
    }

}
