package com.metamapa.monitoring;

import com.metamapa.monitoring.healthindicators.DatabaseHealthIndicator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/monitor")
public class MonitorAdminController {

    private final DatabaseHealthIndicator database;


    public MonitorAdminController(
            DatabaseHealthIndicator database) {

        this.database = database;

    }

    // ---------- FALLAS ----------

    @PostMapping("/fail/database")
    public void failDatabase() {

        log.info("Simulando falla en la base de datos");
        database.forceDown();
    }


    // ---------- RECUPERACIÓN ----------

    @PostMapping("/recover/all")
    public void recoverAll() {
        log.info("Simulando recuperación de todas las dependencias");
        database.recover();
    }
}
