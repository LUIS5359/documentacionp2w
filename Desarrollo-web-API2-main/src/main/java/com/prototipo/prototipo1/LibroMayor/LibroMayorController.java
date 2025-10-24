package com.prototipo.prototipo1.LibroMayor;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize; // <-- seguridad por rol
import org.springframework.web.bind.annotation.*;

import com.prototipo.prototipo1.Cuenta.Cuenta;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/libro-mayor")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LibroMayorController {

    private final LibroMayorRepository libroMayorRepository;
    private final LibroMayorService libroMayorService;

    // GET /api/libro-mayor -> todos los movimientos
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @GetMapping
    public ResponseEntity<List<LibroMayor>> getAll() {
        return ResponseEntity.ok(libroMayorRepository.findAll());
    }

    // GET /api/libro-mayor/{cuentaId} -> movimientos por cuenta
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @GetMapping("/{cuentaId}")
    public ResponseEntity<List<LibroMayor>> getByCuenta(@PathVariable Integer cuentaId) {
        return ResponseEntity.ok(libroMayorRepository.findByCuenta_CuentaId(cuentaId));
    }

    // GET /api/libro-mayor/saldo/{cuentaId} -> saldo total por cuenta
    // (corregido: antes tenía "/api/libro-mayor/saldo/{cuentaId}" con duplicado del
    // prefijo)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @GetMapping("/saldo/{cuentaId}")
    public ResponseEntity<Map<String, Object>> getSaldoByCuenta(@PathVariable Integer cuentaId) {
        Cuenta cuenta = new Cuenta();
        cuenta.setCuentaId(cuentaId);
        BigDecimal saldo = libroMayorService.getSaldoTotalByCuenta(cuenta);
        return ResponseEntity.ok(Map.of("cuentaId", cuentaId, "saldo", saldo));
    }

    // Manejo simple de errores (opcional, por consistencia con otros controllers)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Error interno";
        HttpStatus status = (msg.toLowerCase().contains("no existe") || msg.toLowerCase().contains("no se encontró"))
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(Map.of("error", msg));
    }
}
