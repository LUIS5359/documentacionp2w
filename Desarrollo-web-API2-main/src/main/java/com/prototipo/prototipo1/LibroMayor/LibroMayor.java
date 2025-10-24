package com.prototipo.prototipo1.LibroMayor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.prototipo.prototipo1.Cuenta.Cuenta;
import com.prototipo.prototipo1.LibroDiario.LibroDiario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "libro_mayor")
@AllArgsConstructor
@NoArgsConstructor
public class LibroMayor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mayor_id")
    private Integer mayorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "movimientosDiario", "movimientosMayor" })
    private Cuenta cuenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaccion_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private LibroDiario transaccion;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal debito;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal credito;

    // Si es columna calculada en BD:
    @Column(name = "saldo", precision = 15, scale = 2, insertable = false, updatable = false)
    private BigDecimal saldo;
}
