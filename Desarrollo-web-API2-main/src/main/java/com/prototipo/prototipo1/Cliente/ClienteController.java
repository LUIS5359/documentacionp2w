package com.prototipo.prototipo1.Cliente;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService service;

    // --- Listar / Buscar ---
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @GetMapping
    public ResponseEntity<List<Cliente>> getAll(
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
    public ResponseEntity<Cliente> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // --- CRUD ---
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PostMapping
    public ResponseEntity<Cliente> create(@RequestBody Cliente c) {
        return ResponseEntity.ok(service.create(c));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> update(@PathVariable Integer id, @RequestBody Cliente c) {
        return ResponseEntity.ok(service.update(id, c));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Operaciones de saldo ---
    /** Carga (incrementa) el saldo pendiente del cliente. */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PatchMapping("/{id}/cargar")
    public ResponseEntity<Cliente> cargarSaldo(
            @PathVariable Integer id,
            @RequestParam("monto") BigDecimal monto) {
        return ResponseEntity.ok(service.cargarSaldo(id, monto));
    }

    /** Abona (disminuye) el saldo pendiente del cliente. */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PatchMapping("/{id}/abonar")
    public ResponseEntity<Cliente> abonarSaldo(
            @PathVariable Integer id,
            @RequestParam("monto") BigDecimal monto) {
        return ResponseEntity.ok(service.abonarSaldo(id, monto));
    }

    /** Fija el saldo pendiente a un valor específico. */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PatchMapping("/{id}/saldo")
    public ResponseEntity<Cliente> actualizarSaldo(
            @PathVariable Integer id,
            @RequestParam("valor") BigDecimal valor) {
        return ResponseEntity.ok(service.actualizarSaldo(id, valor));
    }
}
