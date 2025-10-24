package com.prototipo.prototipo1.Proveedor;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 👈 necesario para roles
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProveedorController {

    private final ProveedorService service;

    // --- Listar / Buscar ---
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @GetMapping
    public ResponseEntity<List<Proveedor>> getAll(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "correo", required = false) String correo,
            @RequestParam(value = "minSaldo", required = false) BigDecimal minSaldo,
            @RequestParam(value = "maxSaldo", required = false) BigDecimal maxSaldo) {

        if (q != null && !q.isBlank())
            return ResponseEntity.ok(service.buscarPorNombre(q));

        if (correo != null && !correo.isBlank())
            return ResponseEntity.ok(List.of(service.buscarPorCorreo(correo)));

        if (minSaldo != null || maxSaldo != null)
            return ResponseEntity.ok(service.buscarPorRangoSaldo(minSaldo, maxSaldo));

        return ResponseEntity.ok(service.getAll());
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // --- CRUD ---
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PostMapping
    public ResponseEntity<Proveedor> create(@RequestBody Proveedor p) {
        return ResponseEntity.ok(service.create(p));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> update(@PathVariable Integer id, @RequestBody Proveedor p) {
        return ResponseEntity.ok(service.update(id, p));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Operaciones de saldo ---
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PatchMapping("/{id}/cargar")
    public ResponseEntity<Proveedor> cargarSaldo(@PathVariable Integer id,
            @RequestParam("monto") BigDecimal monto) {
        return ResponseEntity.ok(service.cargarSaldo(id, monto));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PatchMapping("/{id}/abonar")
    public ResponseEntity<Proveedor> abonarSaldo(@PathVariable Integer id,
            @RequestParam("monto") BigDecimal monto) {
        return ResponseEntity.ok(service.abonarSaldo(id, monto));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PatchMapping("/{id}/saldo")
    public ResponseEntity<Proveedor> actualizarSaldo(@PathVariable Integer id,
            @RequestParam("valor") BigDecimal valor) {
        return ResponseEntity.ok(service.actualizarSaldo(id, valor));
    }
}
