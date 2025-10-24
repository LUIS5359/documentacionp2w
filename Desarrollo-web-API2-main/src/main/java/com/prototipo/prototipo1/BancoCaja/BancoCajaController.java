package com.prototipo.prototipo1.BancoCaja;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bancos-caja")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BancoCajaController {

    private final BancoCajaService service;

    // --- Listar / Filtrar ---
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR','CAJERO')")
    @GetMapping
    public ResponseEntity<List<BancoCajaDTO>> getAll(
            @RequestParam(value = "bancoId", required = false) Integer bancoId,
            @RequestParam(value = "tipo", required = false) String tipo,
            @RequestParam(value = "desde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(value = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        if (bancoId != null || (tipo != null && !tipo.isBlank()) || desde != null || hasta != null) {
            return ResponseEntity.ok(service.buscar(bancoId, tipo, desde, hasta));
        }
        return ResponseEntity.ok(service.getAll());
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR','CAJERO')")
    @GetMapping("/{id}")
    public ResponseEntity<BancoCajaDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // --- CRUD ---
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR','CAJERO')")
    @PostMapping
    public ResponseEntity<BancoCajaDTO> create(@RequestBody BancoCajaDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<BancoCajaDTO> update(@PathVariable Integer id, @RequestBody BancoCajaDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Resúmenes ---
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR','CAJERO')")
    @GetMapping("/resumen/total")
    public ResponseEntity<BigDecimal> total(
            @RequestParam(value = "desde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(value = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(value = "bancoId", required = false) Integer bancoId) {
        return ResponseEntity.ok(service.total(desde, hasta, bancoId));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR','CAJERO')")
    @GetMapping("/resumen/por-tipo")
    public ResponseEntity<Map<String, BigDecimal>> totalPorTipo(
            @RequestParam(value = "desde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(value = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(value = "bancoId", required = false) Integer bancoId) {
        return ResponseEntity.ok(service.totalPorTipo(desde, hasta, bancoId));
    }
}