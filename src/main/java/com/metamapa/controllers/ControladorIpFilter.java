package com.metamapa.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * controlador p/ consultar el estado del filtro de ips
 * mas para debugging y verificacion de la configuracion
 */
@RestController
@RequestMapping("/api/admin/ip-filter")
public class ControladorIpFilter {

    @Value("${security.ip.whitelist:}")
    private String whitelistConfig;

    @Value("${security.ip.blacklist:}")
    private String blacklistConfig;

    @Value("${security.ip.filter.enabled:false}")
    private boolean filterEnabled;

    /**
     * obtengo el estado actual del filtro de ips(habilitado/deshabilitado)
     * junto con las listas de ips configuradas (whitelist y blacklist)
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("enabled", filterEnabled);
        status.put("whitelist", parseIpList(whitelistConfig));
        status.put("blacklist", parseIpList(blacklistConfig));
        return ResponseEntity.ok(status);
    }

    /**
     * verifica si una ip seria permitida o bloqueada
     */
    @GetMapping("/check/{ip}")
    public ResponseEntity<Map<String, Object>> checkIp(@PathVariable String ip) {
        // Limpiar la IP de espacios y saltos de línea
        String cleanIp = ip.trim().replace("\n", "").replace("\r", "");

        Map<String, Object> result = new HashMap<>();
        result.put("ip", cleanIp);

        if (!filterEnabled) {
            result.put("status", "ALLOWED");
            result.put("reason", "Filtro de IP deshabilitado");
            return ResponseEntity.ok(result);
        }

        List<String> whitelist = parseIpList(whitelistConfig);
        List<String> blacklist = parseIpList(blacklistConfig);

        if (!blacklist.isEmpty() && blacklist.contains(cleanIp)) {
            result.put("status", "BLOCKED");
            result.put("reason", "IP en lista negra (blacklist)");
        } else if (!whitelist.isEmpty() && !whitelist.contains(cleanIp)) {
            result.put("status", "BLOCKED");
            result.put("reason", "IP no está en lista blanca (whitelist)");
        } else {
            result.put("status", "ALLOWED");
            result.put("reason", "IP permitida");
        }

        return ResponseEntity.ok(result);
    }

    private List<String> parseIpList(String ipConfig) {
        if (ipConfig == null || ipConfig.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(ipConfig.split(","))
                .map(String::trim)
                .filter(ip -> !ip.isEmpty())
                .toList();
    }
}
