package com.prototipo.prototipo1.BalanceSaldo;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/balance-saldos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BalanceSaldoController {

    private final BalanceSaldoService balanceSaldoService;

    // --- Generar balance (solo ADMIN o CONTADOR) ---
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @PostMapping("/generar")
    public ResponseEntity<List<BalanceSaldoDTO>> generar(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        List<BalanceSaldo> balances = balanceSaldoService.generarBalance(fecha);
        
        // Convertir entidades a DTOs
        List<BalanceSaldoDTO> dtos = balances.stream()
                .map(BalanceSaldoDTO::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    // --- Listar todos los balances (ADMIN, CONTADOR, CAJERO) ---
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR','CAJERO')")
    @GetMapping
    public ResponseEntity<List<BalanceSaldoDTO>> getAllBalances() {
        List<BalanceSaldo> balances = balanceSaldoService.getAllBalances();
        
        // Convertir entidades a DTOs
        List<BalanceSaldoDTO> dtos = balances.stream()
                .map(BalanceSaldoDTO::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    // --- Ver balance de una cuenta (ADMIN, CONTADOR, CAJERO) ---
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR','CAJERO')")
    @GetMapping("/{cuentaId}")
    public ResponseEntity<Map<String, Object>> getBalancePorCuenta(@PathVariable Integer cuentaId) {
        BalanceSaldo balance = balanceSaldoService.getBalanceByCuentaId(cuentaId);

        if (balance == null) {
            return ResponseEntity.ok(Map.of("mensaje", "No hay balance generado para esta cuenta."));
        }
        
        // Convertir a DTO
        BalanceSaldoDTO dto = new BalanceSaldoDTO(balance);
        
        return ResponseEntity.ok(Map.of(
                "balanceId", dto.getBalanceId(),
                "fecha", dto.getFecha(),
                "cuentaId", dto.getCuentaId(),
                "codigoCuenta", dto.getCodigoCuenta(),
                "nombreCuenta", dto.getNombreCuenta(),
                "tipoCuenta", dto.getTipoCuenta(),
                "saldoDeudor", dto.getSaldoDeudor(),
                "saldoAcreedor", dto.getSaldoAcreedor()));
    }
}