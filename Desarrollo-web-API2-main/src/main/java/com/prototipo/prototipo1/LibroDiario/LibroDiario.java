package com.prototipo.prototipo1.LibroDiario;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.prototipo.prototipo1.Cuenta.Cuenta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "libro_diario")
@AllArgsConstructor
@NoArgsConstructor
public class LibroDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transaccion_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "movimientosDiario", "movimientosMayor" })
    private Cuenta cuenta;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private String tipo_operacion; // Ejemplo: 'Ingreso', 'Gasto', 'Compra', 'Venta'

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal debito;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal credito;

    @Column(name = "documento_respaldo")
    private String documentoRespaldo;
}
