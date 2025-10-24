package com.prototipo.prototipo1.Banco;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bancos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BancoController {

    private final BancoService service;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR','CAJERO')")
    @GetMapping
    public ResponseEntity<List<Banco>> getAll(
            @RequestParam(value = "activos", defaultValue = "false") boolean soloActivos) {
        if (soloActivos) {
            return ResponseEntity.ok(service.getAllActivos());
        }
        return ResponseEntity.ok(service.getAll());
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR','CAJERO')")
    @GetMapping("/{id}")
    public ResponseEntity<Banco> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PostMapping
    public ResponseEntity<Banco> create(@RequestBody Banco banco) {
        return ResponseEntity.ok(service.create(banco));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Banco> update(@PathVariable Integer id, @RequestBody Banco banco) {
        return ResponseEntity.ok(service.update(id, banco));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}