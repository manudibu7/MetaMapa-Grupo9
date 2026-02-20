package com.metamapa.monitoring.healthindicators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

@Slf4j
public abstract class AbstractDependencyHealthIndicator implements HealthIndicator {
    private boolean lastReportedDown = false;

    private boolean forceDown = false;

    @Override
    public Health health() {

        // ---- Estado forzado por admin ----
        if (forceDown) {
            if (!lastReportedDown) {
                log.warn("Estado DOWN forzado por ADMIN para '{}'", dependencyName());
                lastReportedDown = true;
            }
            return markDown();
        }

        try {
            boolean disponible = estaDisponible();

            if (disponible) {
                if (lastReportedDown) {
                    log.info("Dependencia '{}' recuperada", dependencyName());
                    lastReportedDown = false;
                }
                return markUp();
            }

            // Si está DOWN real
            if (!lastReportedDown) {
                log.warn("Dependency '{}' reported DOWN: {}", dependencyName(), downMessage());
                lastReportedDown = true;
            }

            return markDown();

        } catch (Exception ex) {
            if (!lastReportedDown) {
                log.error("Error comprobando dependencia '{}': {}", dependencyName(), ex.getMessage(), ex);
                lastReportedDown = true;
            }
            return Health.down(ex)
                    .withDetail(dependencyName(), downMessage())
                    .build();
        }
    }


    protected abstract String dependencyName();
    protected abstract String downMessage();

    public abstract boolean estaDisponible();

    public Health markDown() {
        return Health.down()
                .withDetail(dependencyName(), downMessage())
                .build();
    }

    public Health markUp() {
        return Health.up()
                .withDetail(dependencyName(), "OK")
                .build();
    }
    public void forceDown() {
        log.warn("Forzando estado DOWN para '{}'", dependencyName());
        this.forceDown= true;
        // En este diseño, el estado forzado se maneja a través de un flag que se verifica en el método health()
        // Esto permite que el estado forzado persista hasta que se recupere manualmente o se reinicie el servicio
    }
    public void recover(){
        log.info("Recuperando estado para '{}'", dependencyName());
        this.forceDown= false;
        // En este diseño, la recuperación se maneja a través de un método que podría resetear el flag de forzado
        // y permitir que el health check normal vuelva a determinar el estado real de la dependencia
    }

    protected boolean getForceDown() {
        return this.forceDown;
    }
}
