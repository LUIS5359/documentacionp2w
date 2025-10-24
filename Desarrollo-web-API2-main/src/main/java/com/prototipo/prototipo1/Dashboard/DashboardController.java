package com.prototipo.prototipo1.Dashboard;

import com.prototipo.prototipo1.Dashboard.dto.DashboardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*") // En producción, especifica los orígenes permitidos
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    /**
     * Endpoint principal para obtener las métricas del dashboard según el rol del usuario autenticado.
     * 
     * @param authentication El objeto de autenticación de Spring Security
     * @return ResponseEntity con las métricas del dashboard
     */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR','CAJERO')")
    @GetMapping("/metrics")
    public ResponseEntity<?> metrics(Authentication authentication) {
        try {
            // Validación de autenticación
            if (authentication == null) {
                log.warn("Intento de acceso sin autenticación");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("No autenticado", "Se requiere autenticación"));
            }

            if (authentication.getAuthorities() == null || authentication.getAuthorities().isEmpty()) {
                log.warn("Usuario autenticado sin roles: {}", authentication.getName());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("Sin roles", "El usuario no tiene roles asignados"));
            }

            // Obtener el primer rol (ajusta si manejas múltiples roles)
            String authority = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority) // p.ej. "ROLE_ADMINISTRADOR"
                    .findFirst()
                    .orElse("ROLE_USER");

            log.info("Usuario {} accediendo a dashboard con rol: {}", 
                    authentication.getName(), authority);

            // Construir respuesta según rol
            DashboardResponse response = service.buildForRole(authority);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al procesar métricas del dashboard: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error interno", 
                            "No se pudieron cargar las métricas del dashboard"));
        }
    }

    /**
     * Endpoint alternativo que devuelve TODAS las métricas (útil para testing o admin supremo).
     * Solo accesible por ADMINISTRADOR.
     */
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/metrics/all")
    public ResponseEntity<?> allMetrics(Authentication authentication) {
        try {
            log.info("Usuario {} solicitando todas las métricas", authentication.getName());
            DashboardResponse response = service.buildAll();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al procesar todas las métricas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error interno", 
                            "No se pudieron cargar todas las métricas"));
        }
    }

    /**
     * Health check del dashboard
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "Dashboard");
        status.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(status);
    }

    /**
     * Helper para crear respuestas de error consistentes
     */
    private Map<String, String> createErrorResponse(String error, String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
        return errorResponse;
    }
}