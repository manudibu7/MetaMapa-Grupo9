package com.metamapa.monitoring.healthindicators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

@Slf4j
public abstract class AbstractDependencyHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        try {
            if (estaDisponible()) {
                return markUp();
            }

            log.warn("Dependency '{}' reported DOWN: {}", dependencyName(), downMessage());
            return markDown();

        } catch (Exception ex) {
            log.error("Error comprobando dependencia '{}': {}", dependencyName(), ex.getMessage(), ex);
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
}
