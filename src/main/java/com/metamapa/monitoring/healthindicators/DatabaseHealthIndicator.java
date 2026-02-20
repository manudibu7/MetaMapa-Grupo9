package com.metamapa.monitoring.healthindicators;


import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("database")
public class DatabaseHealthIndicator extends AbstractDependencyHealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected String dependencyName() {
        return "database";
    }

    @Override
    protected String downMessage() {
        return "Database no disponible o sin respuesta al SELECT 1";
    }

    @Override
    public boolean estaDisponible() {
        if (this.getForceDown()) {
            log.warn("Estado DOWN forzado por ADMIN para 'database'");
            return false;
        }
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            boolean ok = result != null && result == 1;
            if (!ok) {
                log.error("Database health check returned unexpected result: {}", result);
            }
            return ok;
        } catch (Exception ex) {
            log.error("Database health check failed: {}", ex.getMessage(), ex);
            return false;
        }
    }
}
