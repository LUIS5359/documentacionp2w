package com.prototipo.prototipo1.LibroDiario;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <-- importante
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/libro-diario")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LibroDiarioController {

    private final LibroDiarioService libroDiarioService;

    // GET /api/libro-diario
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @GetMapping
    public ResponseEntity<List<LibroDiario>> getAll() {
        return ResponseEntity.ok(libroDiarioService.getAllLibroDiarios());
    }

    // GET /api/libro-diario/{id}
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<LibroDiario> getById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(libroDiarioService.getLibroDiarioId(id));
    }

    // POST /api/libro-diario
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PostMapping
    public ResponseEntity<LibroDiario> create(@RequestBody LibroDiario body) {
        LibroDiario creado = libroDiarioService.createLibroDiario(body);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/libro-diario/" + creado.getTransaccion_id()));
        return new ResponseEntity<>(creado, headers, HttpStatus.CREATED);
    }

    // PUT /api/libro-diario/{id}
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<LibroDiario> update(@PathVariable("id") Integer id,
            @RequestBody LibroDiario body) {
        LibroDiario actualizado = libroDiarioService.updateLibroDiario(id, body);
        return ResponseEntity.ok(actualizado);
    }

    // DELETE /api/libro-diario/{id}
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable("id") Integer id) {
        libroDiarioService.deleteLibroDiario(id);
        return ResponseEntity.ok(Map.of("deleted", true, "id", id));
    }

    // ---- Manejo simple de errores de negocio ----
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Error interno";
        HttpStatus status = msg.toLowerCase().contains("no se encontró") ||
                msg.toLowerCase().contains("no existe")
                        ? HttpStatus.NOT_FOUND
                        : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(Map.of("error", msg));
    }
}
