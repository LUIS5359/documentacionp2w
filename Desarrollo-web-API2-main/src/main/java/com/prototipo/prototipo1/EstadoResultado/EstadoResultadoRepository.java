package com.prototipo.prototipo1.EstadoResultado;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface EstadoResultadoRepository extends JpaRepository<EstadoResultado, Integer> {

    // INGRESOS: cuentas tipo 'Ingreso' => sum(credito - debito)
    @Query(value = """
            SELECT COALESCE(SUM(lm.credito - lm.debito), 0)
            FROM libro_mayor lm
            JOIN cuentas c ON c.cuenta_id = lm.cuenta_id
            WHERE c.tipo_cuenta = 'Ingreso'
              AND lm.fecha BETWEEN :inicio AND :fin
            """, nativeQuery = true)
    BigDecimal sumIngresos(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // COSTOS: cuentas tipo 'Gasto' cuyo nombre contenga 'costo' => sum(debito -
    // credito)
    @Query(value = """
            SELECT COALESCE(SUM(lm.debito - lm.credito), 0)
            FROM libro_mayor lm
            JOIN cuentas c ON c.cuenta_id = lm.cuenta_id
            WHERE c.tipo_cuenta = 'Gasto'
              AND LOWER(c.nombre_cuenta) LIKE '%costo%'
              AND lm.fecha BETWEEN :inicio AND :fin
            """, nativeQuery = true)
    BigDecimal sumCostos(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // GASTOS: cuentas tipo 'Gasto' cuyo nombre NO contenga 'costo' => sum(debito -
    // credito)
    @Query(value = """
            SELECT COALESCE(SUM(lm.debito - lm.credito), 0)
            FROM libro_mayor lm
            JOIN cuentas c ON c.cuenta_id = lm.cuenta_id
            WHERE c.tipo_cuenta = 'Gasto'
              AND LOWER(c.nombre_cuenta) NOT LIKE '%costo%'
              AND lm.fecha BETWEEN :inicio AND :fin
            """, nativeQuery = true)
    BigDecimal sumGastos(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}
