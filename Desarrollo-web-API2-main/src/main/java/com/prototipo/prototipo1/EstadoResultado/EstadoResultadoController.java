package com.prototipo.prototipo1.EstadoResultado;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <-- IMPORTANTE
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/estado-resultados")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EstadoResultadoController {

    private final EstadoResultadoService service;

    // --- Lectura ---
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @GetMapping
    public ResponseEntity<List<EstadoResultado>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<EstadoResultado> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // --- Escritura ---
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PostMapping
    public ResponseEntity<EstadoResultado> create(@RequestBody EstadoResultado er) {
        return ResponseEntity.ok(service.create(er));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<EstadoResultado> update(@PathVariable Integer id,
            @RequestBody EstadoResultado er) {
        return ResponseEntity.ok(service.update(id, er));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** Genera y guarda el Estado de Resultados calculado por periodo */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PostMapping("/generar")
    public ResponseEntity<EstadoResultado> generar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        // (Opcional) Validación rápida de periodo para evitar 400 innecesarios en el
        // service
        if (inicio == null || fin == null || fin.isBefore(inicio)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.generarYGuardar(inicio, fin));
    }
}
