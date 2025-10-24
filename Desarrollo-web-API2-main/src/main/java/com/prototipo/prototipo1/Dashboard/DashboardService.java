package com.prototipo.prototipo1.Dashboard;

import com.prototipo.prototipo1.Dashboard.dto.DashboardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository repo;

    // ----------------- Helpers de seguridad/null-safety -----------------

    /**
     * Normaliza la autoridad a un rol sin prefijo (ROLE_). 
     * Ej: "ROLE_ADMINISTRADOR" -> "ADMINISTRADOR".
     */
    private String normalizeRole(String authority) {
        if (authority == null || authority.isBlank()) {
            log.warn("Authority es null o vacío, usando USER por defecto");
            return "USER";
        }
        String normalized = authority.startsWith("ROLE_") ? authority.substring(5) : authority;
        log.debug("Rol normalizado: {} -> {}", authority, normalized);
        return normalized;
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private Integer nzi(Integer v) {
        return v == null ? 0 : v;
    }

    private <T> List<T> nzl(List<T> v) {
        return v == null ? Collections.emptyList() : v;
    }

    // ----------------- API pública -----------------

    /**
     * Construye el dashboard según el rol proveniente del JWT.
     * Devuelve ceros/listas vacías en campos no aplicables al rol.
     */
    @Transactional(readOnly = true)
    public DashboardResponse buildForRole(String authority) {
        final String role = normalizeRole(authority);
        final LocalDate hoy = LocalDate.now();
        
        log.info("Construyendo dashboard para rol: {}", role);

        // Builder con defaults seguros
        DashboardResponse.DashboardResponseBuilder b = DashboardResponse.builder()
                .ventasMes(BigDecimal.ZERO)
                .gastosMes(BigDecimal.ZERO)
                .valorInventario(BigDecimal.ZERO)
                .saldoBanco(BigDecimal.ZERO)
                .asientosDia(0)
                .descuadreDia(BigDecimal.ZERO)
                .ultimosAsientos(Collections.emptyList())
                .cobrosHoy(BigDecimal.ZERO)
                .pagosHoy(BigDecimal.ZERO)
                .topPorCobrar(Collections.emptyList())
                .topPorPagar(Collections.emptyList());

        try {
            switch (role) {
                case "ADMINISTRADOR" -> {
                    log.debug("Cargando métricas de ADMINISTRADOR");
                    b.ventasMes(nz(repo.ventasMes()));
                    b.gastosMes(nz(repo.gastosMes()));
                    b.valorInventario(nz(repo.valorInventario()));
                    // Cambié a código de cuenta correcto (1002 según tu SQL)
                    b.saldoBanco(nz(repo.saldoPorCodigo("1002")));
                    
                    // El admin también ve los últimos asientos
                    var itemsAdmin = nzl(repo.ultimosAsientos()).stream()
                            .map(a -> new DashboardResponse.AsientoItem(
                                    a.getTransaccionId(),
                                    a.getFecha(),
                                    a.getDescripcion(),
                                    nz(a.getDebito()),
                                    nz(a.getCredito())))
                            .toList();
                    b.ultimosAsientos(itemsAdmin);
                }
                case "CONTADOR" -> {
                    log.debug("Cargando métricas de CONTADOR");
                    b.asientosDia(nzi(repo.asientosDia(hoy)));
                    b.descuadreDia(nz(repo.descuadreDia(hoy)));
                    
                    var items = nzl(repo.ultimosAsientos()).stream()
                            .map(a -> new DashboardResponse.AsientoItem(
                                    a.getTransaccionId(),
                                    a.getFecha(),
                                    a.getDescripcion(),
                                    nz(a.getDebito()),
                                    nz(a.getCredito())))
                            .toList();
                    b.ultimosAsientos(items);
                }
                case "CAJERO" -> {
                    log.debug("Cargando métricas de CAJERO");
                    b.cobrosHoy(nz(repo.cobrosHoy(hoy)));
                    b.pagosHoy(nz(repo.pagosHoy(hoy)));
                    
                    b.topPorCobrar(
                            nzl(repo.topPorCobrar()).stream()
                                    .map(t -> new DashboardResponse.TopItem(
                                            t.getNombre(), 
                                            nz(t.getSaldoPendiente())))
                                    .toList());
                    
                    b.topPorPagar(
                            nzl(repo.topPorPagar()).stream()
                                    .map(t -> new DashboardResponse.TopItem(
                                            t.getNombre(), 
                                            nz(t.getSaldoPendiente())))
                                    .toList());
                }
                default -> {
                    log.warn("Rol desconocido: {}. Devolviendo dashboard vacío", role);
                }
            }
        } catch (Exception e) {
            log.error("Error al construir dashboard para rol {}: {}", role, e.getMessage(), e);
            // Devuelve el builder con valores por defecto en caso de error
        }

        DashboardResponse response = b.build();
        log.info("Dashboard construido exitosamente para rol: {}", role);
        return response;
    }

    /**
     * Útil si el frontend decide qué mostrar según el rol.
     * Devuelve TODAS las métricas de todos los roles.
     */
    @Transactional(readOnly = true)
    public DashboardResponse buildAll() {
        final LocalDate hoy = LocalDate.now();
        log.info("Construyendo dashboard completo (todos los roles)");
        
        try {
            return DashboardResponse.builder()
                    // ADMIN
                    .ventasMes(nz(repo.ventasMes()))
                    .gastosMes(nz(repo.gastosMes()))
                    .valorInventario(nz(repo.valorInventario()))
                    .saldoBanco(nz(repo.saldoPorCodigo("1002")))
                    // CONTADOR
                    .asientosDia(nzi(repo.asientosDia(hoy)))
                    .descuadreDia(nz(repo.descuadreDia(hoy)))
                    .ultimosAsientos(
                            nzl(repo.ultimosAsientos()).stream()
                                    .map(a -> new DashboardResponse.AsientoItem(
                                            a.getTransaccionId(),
                                            a.getFecha(),
                                            a.getDescripcion(),
                                            nz(a.getDebito()),
                                            nz(a.getCredito())))
                                    .toList())
                    // CAJERO
                    .cobrosHoy(nz(repo.cobrosHoy(hoy)))
                    .pagosHoy(nz(repo.pagosHoy(hoy)))
                    .topPorCobrar(
                            nzl(repo.topPorCobrar()).stream()
                                    .map(t -> new DashboardResponse.TopItem(
                                            t.getNombre(), 
                                            nz(t.getSaldoPendiente())))
                                    .toList())
                    .topPorPagar(
                            nzl(repo.topPorPagar()).stream()
                                    .map(t -> new DashboardResponse.TopItem(
                                            t.getNombre(), 
                                            nz(t.getSaldoPendiente())))
                                    .toList())
                    .build();
        } catch (Exception e) {
            log.error("Error al construir dashboard completo: {}", e.getMessage(), e);
            // Devuelve dashboard vacío pero seguro
            return DashboardResponse.builder()
                    .ventasMes(BigDecimal.ZERO)
                    .gastosMes(BigDecimal.ZERO)
                    .valorInventario(BigDecimal.ZERO)
                    .saldoBanco(BigDecimal.ZERO)
                    .asientosDia(0)
                    .descuadreDia(BigDecimal.ZERO)
                    .ultimosAsientos(Collections.emptyList())
                    .cobrosHoy(BigDecimal.ZERO)
                    .pagosHoy(BigDecimal.ZERO)
                    .topPorCobrar(Collections.emptyList())
                    .topPorPagar(Collections.emptyList())
                    .build();
        }
    }
}