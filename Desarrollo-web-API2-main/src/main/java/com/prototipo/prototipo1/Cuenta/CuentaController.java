package com.prototipo.prototipo1.Cuenta;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <-- IMPORTANTE
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;

    // GET /api/cuentas -> lista todas
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @GetMapping
    public ResponseEntity<List<Cuenta>> getAll() {
        return ResponseEntity.ok(cuentaService.getAllCuentas());
    }

    // GET /api/cuentas/{id} -> por id
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<Cuenta> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(cuentaService.getCuentaById(id));
    }

    // GET /api/cuentas/by-codigo/{codigo} -> por código único
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @GetMapping("/by-codigo/{codigo}")
    public ResponseEntity<Cuenta> getByCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(cuentaService.getByCodigo(codigo));
    }

    // POST /api/cuentas -> crear
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PostMapping
    public ResponseEntity<Cuenta> create(@RequestBody Cuenta body) {
        Cuenta creada = cuentaService.createCuenta(body);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/cuentas/" + creada.getCuentaId()));
        return new ResponseEntity<>(creada, headers, HttpStatus.CREATED);
    }

    // PUT /api/cuentas/{id} -> actualizar
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Cuenta> update(@PathVariable Integer id, @RequestBody Cuenta body) {
        return ResponseEntity.ok(cuentaService.updateCuenta(id, body));
    }

    // DELETE /api/cuentas/{id} -> eliminar
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        cuentaService.deleteCuenta(id);
        return ResponseEntity.ok(Map.of("deleted", true, "id", id));
    }

    // -- Manejo simple de errores de negocio (404/400) --
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Error";
        HttpStatus status = (msg.toLowerCase().contains("no existe") || msg.toLowerCase().contains("no encontrada"))
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(Map.of("error", msg));
    }
}
