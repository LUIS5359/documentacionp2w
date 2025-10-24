package com.prototipo.prototipo1.BalanceSaldo;

import com.prototipo.prototipo1.Cuenta.Cuenta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "balance_saldos")
@AllArgsConstructor
@NoArgsConstructor
public class BalanceSaldo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "balance_id")
    private Integer balanceId;

    @Column(nullable = false)
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private Cuenta cuenta;

    @Column(name = "saldo_deudor", precision = 15, scale = 2)
    private BigDecimal saldoDeudor;

    @Column(name = "saldo_acreedor", precision = 15, scale = 2)
    private BigDecimal saldoAcreedor;
}
