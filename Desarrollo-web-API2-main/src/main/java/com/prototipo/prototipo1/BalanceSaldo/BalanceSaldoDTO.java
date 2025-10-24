package com.prototipo.prototipo1.BalanceSaldo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceSaldoDTO {
    private Integer balanceId;
    private LocalDate fecha;
    private Integer cuentaId;
    private String codigoCuenta;
    private String nombreCuenta;
    private String tipoCuenta;
    private BigDecimal saldoDeudor;
    private BigDecimal saldoAcreedor;
    
    // Constructor para mapeo fácil desde la entidad
    public BalanceSaldoDTO(BalanceSaldo balance) {
        this.balanceId = balance.getBalanceId();
        this.fecha = balance.getFecha();
        this.cuentaId = balance.getCuenta().getCuentaId();
        this.codigoCuenta = balance.getCuenta().getCodigoCuenta();
        this.nombreCuenta = balance.getCuenta().getNombreCuenta();
        this.tipoCuenta = balance.getCuenta().getTipoCuenta();
        this.saldoDeudor = balance.getSaldoDeudor();
        this.saldoAcreedor = balance.getSaldoAcreedor();
    }
}