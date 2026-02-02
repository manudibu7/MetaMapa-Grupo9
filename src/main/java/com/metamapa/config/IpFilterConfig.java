package com.metamapa.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * filtro de control de acceso por dirs ip
 * defino listas de direcciones ips autorizadas (whitelist) y no autorizadas (blacklist),
 * bloqueando solicitudes Http provenientes de fuentes q no quiera (no confiables o maliciosas.
 */
@Component
public class IpFilterConfig extends OncePerRequestFilter {

    @Value("${security.ip.whitelist:}")
    private String whitelistConfig;

    @Value("${security.ip.blacklist:}")
    private String blacklistConfig;

    @Value("${security.ip.filter.enabled:false}")
    private boolean filterEnabled;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // si el filtro no está habilitado, continuar normalmente
        if (!filterEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        List<String> whitelist = parseIpList(whitelistConfig);
        List<String> blacklist = parseIpList(blacklistConfig);

        // verifico blacklist primero (ips bloqueadas explicitamente)
        if (!blacklist.isEmpty() && isIpInList(clientIp, blacklist)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Acceso denegado: IP bloqueada\", \"ip\": \"" + clientIp + "\"}");
            return;
        }

        // si hay whitelist configurada, verificar que la ip esta incluida
        if (!whitelist.isEmpty() && !isIpInList(clientIp, whitelist)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Acceso denegado: IP no autorizada\", \"ip\": \"" + clientIp + "\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * obtiene la ip del cliente, considerando proxies (X-Forwarded-For?)
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For puede contener varias ips, la primera es la del cliente original
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp.trim();
        }

        return request.getRemoteAddr();
    }

    /**
     * parsea una lista de ips separadas por coma.
     */
    private List<String> parseIpList(String ipConfig) {
        if (ipConfig == null || ipConfig.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(ipConfig.split(","))
                .map(String::trim)
                .filter(ip -> !ip.isEmpty())
                .toList();
    }

    /**
     * verifico si una ip esta en la lista, (soporto ips individuales y rangos CIDR).
     */
    private boolean isIpInList(String clientIp, List<String> ipList) {
        for (String ip : ipList) {
            if (ip.contains("/")) {
                // Soporte básico para rangos CIDR
                if (isIpInCidrRange(clientIp, ip)) {
                    return true;
                }
            } else if (ip.equals(clientIp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * verifica si una ip esta dentro de un rango CIDR.
     * x ejemplo: 192.168.1.0/24 incluye 192.168.1.1 a 192.168.1.255
     */
    private boolean isIpInCidrRange(String clientIp, String cidr) {
        try {
            String[] parts = cidr.split("/");
            String networkIp = parts[0];
            int prefixLength = Integer.parseInt(parts[1]);

            long clientIpLong = ipToLong(clientIp);
            long networkIpLong = ipToLong(networkIp);
            long mask = (-1L) << (32 - prefixLength);

            return (clientIpLong & mask) == (networkIpLong & mask);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * convierto ip string a long
     */
    private long ipToLong(String ip) {
        String[] octets = ip.split("\\.");
        if (octets.length != 4) {
            throw new IllegalArgumentException("Invalid IP address: " + ip);
        }
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) | Integer.parseInt(octets[i]);
        }
        return result;
    }
}
