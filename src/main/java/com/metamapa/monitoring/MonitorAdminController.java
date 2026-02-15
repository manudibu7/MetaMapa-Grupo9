package com.metamapa.monitoring;

import com.metamapa.monitoring.healthindicators.DatabaseHealthIndicator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
        database.markDown();
    }


    // ---------- RECUPERACIÓN ----------

    @PostMapping("/recover/all")
    public void recoverAll() {
        database.markUp();
    }
}
