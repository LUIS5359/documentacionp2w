package com.prototipo.prototipo1.EstadoResultado;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "estado_resultados")
@AllArgsConstructor
@NoArgsConstructor
public class EstadoResultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "estado_resultados_id")
    private Integer estadoResultadosId;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "ingresos_totales", precision = 15, scale = 2, nullable = false)
    private BigDecimal ingresosTotales;

    @Column(name = "costos_totales", precision = 15, scale = 2, nullable = false)
    private BigDecimal costosTotales;

    @Column(name = "gastos_totales", precision = 15, scale = 2, nullable = false)
    private BigDecimal gastosTotales;

    @Column(name = "resultado_neto", precision = 15, scale = 2, insertable = false, updatable = false)
    private BigDecimal resultadoNeto;
}
