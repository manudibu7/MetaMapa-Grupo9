package com.metamapa.controllers;

import com.metamapa.config.RateLimitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * controlador para administrar el sistema de rate limiting
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/rate-limit")
public class ControladorRateLimit {

    private final RateLimitConfig rateLimitConfig;

    @Autowired
    public ControladorRateLimit(RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
    }

    /**
     * estadssticas generales del sistema de rate limiting
     * GET /api/admin/rate-limit/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {

        return ResponseEntity.ok(rateLimitConfig.getStats());
    }

    /**
     * info de rate limit para una ip especifica
     * GET /api/admin/rate-limit/check/{ip}
     */
    @GetMapping("/check/{ip}")
    public ResponseEntity<Map<String, Object>> checkIp(@PathVariable String ip) {
        log.info("Consultando info de rate limit para IP: {}", ip);
        return ResponseEntity.ok(rateLimitConfig.getIpInfo(ip));
    }

    /**
     * desbloquear manualmente una IP bloqueada
     * POST /api/admin/rate-limit/unblock/{ip}
     */
    @PostMapping("/unblock/{ip}")
    public ResponseEntity<Map<String, Object>> unblockIp(@PathVariable String ip) {
        log.info("Intentando desbloquear IP: {}", ip);
        boolean result = rateLimitConfig.unblockIp(ip);
        return ResponseEntity.ok(Map.of(
            "ip", ip,
            "unblocked", result,
            "message", result ? "IP desbloqueada exitosamente" : "No se pudo desbloquear la IP"
        ));
    }

    /**
     * reset el contador de solicitudes de una ip sin desbloquearla
     * POST /api/admin/rate-limit/reset/{ip}
     */
    @PostMapping("/reset/{ip}")
    public ResponseEntity<Map<String, Object>> resetIpCounter(@PathVariable String ip) {
        log.info("Intentando resetear contador de IP: {}", ip);
        boolean result = rateLimitConfig.resetIpCounter(ip);
        return ResponseEntity.ok(Map.of(
            "ip", ip,
            "reset", result,
            "message", result ? "Contador de IP reseteado exitosamente" : "No se pudo resetear el contador"
        ));
    }
}
