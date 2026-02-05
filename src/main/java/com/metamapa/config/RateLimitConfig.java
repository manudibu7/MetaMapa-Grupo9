package com.metamapa.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Order(1) //p ejecutar antes de otros filtros
public class RateLimitConfig extends OncePerRequestFilter {

    @Value("${security.ratelimit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${security.ratelimit.requests-per-window:100}")
    private int maxRequestsPerWindow;

    @Value("${security.ratelimit.window-seconds:60}")
    private int windowSizeSeconds;

    @Value("${security.ratelimit.block-duration-seconds:300}")
    private int blockDurationSeconds;

    // almacena el estado de rate limit por ip
    private final Map<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();

    // almacena ips bloqueadas temporalmente por exceder el limite
    private final Map<String, Instant> blockedIps = new ConcurrentHashMap<>();

    // servicio para limpiar entradas antiguas periodicamente
    private final ScheduledExecutorService cleanupService = Executors.newSingleThreadScheduledExecutor();

    public RateLimitConfig() {
        // limpiar entradas antiguas cada minuto
        cleanupService.scheduleAtFixedRate(this::cleanupOldEntries, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // si el rl (rate limita no está habilitado, continuar normalmente
        if (!rateLimitEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);

        // verificar si ip esta bloqueada temporalmente
        if (isIpBlocked(clientIp)) {
            respondWithTooManyRequests(response, clientIp, "IP bloqueada temporalmente por exceder límite de solicitudes");
            return;
        }

        // obtener o crear info de rrl para esta ip
        RateLimitInfo rateLimitInfo = rateLimitMap.computeIfAbsent(clientIp, k -> new RateLimitInfo(windowSizeSeconds));

        // verificar si se excedio el límite
        if (!rateLimitInfo.allowRequest(maxRequestsPerWindow)) {
            // bloquear ip temporalmente
            blockedIps.put(clientIp, Instant.now().plusSeconds(blockDurationSeconds));
            respondWithTooManyRequests(response, clientIp, "Límite de solicitudes excedido");
            return;
        }

        // agregar headers de rl en la respuesta
        addRateLimitHeaders(response, rateLimitInfo);

        filterChain.doFilter(request, response);
    }

    /**
     * verifica si una ip esta bloqueada temporalmente
     */
    private boolean isIpBlocked(String clientIp) {
        Instant blockedUntil = blockedIps.get(clientIp);
        if (blockedUntil == null) {
            return false;
        }

        if (Instant.now().isAfter(blockedUntil)) {
            // ll bloqueo expiro, eliminar de l lista
            blockedIps.remove(clientIp);
            rateLimitMap.remove(clientIp); // Resetear contador
            return false;
        }

        return true;
    }

    /**
     * responde con error 429 "Too Many Requests
     */
    private void respondWithTooManyRequests(HttpServletResponse response, String clientIp, String reason) throws IOException {
        response.setStatus(429); // aca seteo el Too Many Requests
        response.setContentType("application/json");
        response.setHeader("Retry-After", String.valueOf(blockDurationSeconds));

        Instant blockedUntil = blockedIps.get(clientIp);
        long retryAfterSeconds = blockedUntil != null
            ? Math.max(0, blockedUntil.getEpochSecond() - Instant.now().getEpochSecond())
            : blockDurationSeconds;

        response.getWriter().write(String.format(
            "{\"error\": \"Too Many Requests\", \"message\": \"%s\", \"ip\": \"%s\", \"retryAfterSeconds\": %d}",
            reason, clientIp, retryAfterSeconds
        ));
    }

    /**
     * agrega headers informativos sobre el rate limit
     */
    private void addRateLimitHeaders(HttpServletResponse response, RateLimitInfo info) {
        response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequestsPerWindow));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, maxRequestsPerWindow - info.getRequestCount())));
        response.setHeader("X-RateLimit-Reset", String.valueOf(info.getWindowResetTime()));
    }

    /**
     * obtiene la ip del cliente, considerando proxies
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp.trim();
        }

        return request.getRemoteAddr();
    }

    /**
     * limpia entradas antiguas del mapa para evitar memory leak
     */
    private void cleanupOldEntries() {
        Instant now = Instant.now();

        // limpiar ips desbloqueadas
        blockedIps.entrySet().removeIf(entry -> now.isAfter(entry.getValue()));

        // limpiar rls inactivos (mas de 2 ventanas sin actividad)
        long maxInactiveMillis = windowSizeSeconds * 2000L;
        rateLimitMap.entrySet().removeIf(entry ->
            (now.toEpochMilli() - entry.getValue().getLastRequestTime()) > maxInactiveMillis
        );
    }

    /**
     * clase interna para almacenar info de rate limit por ip
     * implementa el algoritmo de Sliding Window Log
     */
    private static class RateLimitInfo {
        private final int windowSizeSeconds;
        private final java.util.Deque<Long> requestTimestamps = new java.util.concurrent.ConcurrentLinkedDeque<>();
        private volatile long lastRequestTime;

        public RateLimitInfo(int windowSizeSeconds) {
            this.windowSizeSeconds = windowSizeSeconds;
            this.lastRequestTime = System.currentTimeMillis();
        }

        /**
         * intenta permitir una nueva solicitud
         * @return true si la solicitud es permitida, false si excede el lomite
         */
        public synchronized boolean allowRequest(int maxRequests) {
            long now = System.currentTimeMillis();
            long windowStart = now - (windowSizeSeconds * 1000L);

            // eliminar timestamps fuera de la ventana actual
            while (!requestTimestamps.isEmpty() && requestTimestamps.peekFirst() < windowStart) {
                requestTimestamps.pollFirst();
            }

            // verificar si se puede aceptar la solicitud
            if (requestTimestamps.size() >= maxRequests) {
                return false;
            }

            // registrar la solicitud
            requestTimestamps.addLast(now);
            lastRequestTime = now;
            return true;
        }

        public int getRequestCount() {
            long now = System.currentTimeMillis();
            long windowStart = now - (windowSizeSeconds * 1000L);
            return (int) requestTimestamps.stream().filter(ts -> ts >= windowStart).count();
        }

        public long getWindowResetTime() {
            if (requestTimestamps.isEmpty()) {
                return System.currentTimeMillis() / 1000;
            }
            return (requestTimestamps.peekFirst() + (windowSizeSeconds * 1000L)) / 1000;
        }

        public long getLastRequestTime() {
            return lastRequestTime;
        }
    }

    // MMETODOS DE ADMINISTRACION

    /**
     * estadisticas actuales del rate limiting.
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("enabled", rateLimitEnabled);
        stats.put("maxRequestsPerWindow", maxRequestsPerWindow);
        stats.put("windowSizeSeconds", windowSizeSeconds);
        stats.put("blockDurationSeconds", blockDurationSeconds);
        stats.put("activeIpsTracked", rateLimitMap.size());
        stats.put("blockedIpsCount", blockedIps.size());
        return stats;
    }

    /**
     * obtiene info de ip especifica.
     */
    public Map<String, Object> getIpInfo(String ip) {
        Map<String, Object> info = new java.util.HashMap<>();
        info.put("ip", ip);
        info.put("blocked", isIpBlocked(ip));

        RateLimitInfo rateLimitInfo = rateLimitMap.get(ip);
        if (rateLimitInfo != null) {
            info.put("currentRequests", rateLimitInfo.getRequestCount());
            info.put("remainingRequests", Math.max(0, maxRequestsPerWindow - rateLimitInfo.getRequestCount()));
        } else {
            info.put("currentRequests", 0);
            info.put("remainingRequests", maxRequestsPerWindow);
        }

        Instant blockedUntil = blockedIps.get(ip);
        if (blockedUntil != null) {
            info.put("blockedUntil", blockedUntil.toString());
            info.put("retryAfterSeconds", Math.max(0, blockedUntil.getEpochSecond() - Instant.now().getEpochSecond()));
        }

        return info;
    }

    /**
     * desbloquear ip
     */
    public boolean unblockIp(String ip) {
        blockedIps.remove(ip);
        rateLimitMap.remove(ip);
        return true;
    }

    /**
     * resetear contador de ip
     */
    public boolean resetIpCounter(String ip) {
        rateLimitMap.remove(ip);
        return true;
    }
}
