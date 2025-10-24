package com.prototipo.prototipo1.Inventario;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InventarioController {

    private final InventarioService service;

    // --- Búsquedas ---
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR','CAJERO')")
    @GetMapping
    public ResponseEntity<List<Inventario>> getAll(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "tipo", required = false) String tipo) {
        if (q != null && !q.isBlank())
            return ResponseEntity.ok(service.buscarPorNombre(q));
        if (tipo != null && !tipo.isBlank())
            return ResponseEntity.ok(service.buscarPorTipo(tipo));
        return ResponseEntity.ok(service.getAll());
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR','CAJERO')")
    @GetMapping("/{id}")
    public ResponseEntity<Inventario> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // --- CRUD ---
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PostMapping
    public ResponseEntity<Inventario> create(@RequestBody Inventario p) {
        return ResponseEntity.ok(service.create(p));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Inventario> update(@PathVariable Integer id, @RequestBody Inventario p) {
        return ResponseEntity.ok(service.update(id, p));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Operaciones adicionales ---
    /** Ajusta el stock: delta positivo (ingreso), negativo (egreso). */
    @PatchMapping("/{id}/ajustar-stock")
    public ResponseEntity<Inventario> ajustarStock(
            @PathVariable Integer id,
            @RequestParam("delta") Integer delta) {
        return ResponseEntity.ok(service.ajustarStock(id, delta));
    }

    /** Actualiza el precio unitario. */
    @PatchMapping("/{id}/precio")
    public ResponseEntity<Inventario> actualizarPrecio(
            @PathVariable Integer id,
            @RequestParam("valor") BigDecimal valor) {
        return ResponseEntity.ok(service.actualizarPrecio(id, valor));
    }
}
