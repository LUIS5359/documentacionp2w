package com.prototipo.prototipo1.LibroMayor;

import com.prototipo.prototipo1.Cuenta.Cuenta;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LibroMayorRepository extends JpaRepository<LibroMayor, Integer> {

    // Trae el último movimiento de una cuenta por fecha y luego por id (desc)
    LibroMayor findFirstByCuentaOrderByFechaDescMayorIdDesc(Cuenta cuenta);

    List<LibroMayor> findByCuenta_CuentaId(Integer cuentaId);

    List<LibroMayor> findByCuenta(Cuenta cuentaId);

    @Query(value = """
            SELECT lm.cuenta_id, COALESCE(SUM(lm.debito - lm.credito), 0) AS saldo
            FROM libro_mayor lm
            WHERE lm.fecha <= :corte
            GROUP BY lm.cuenta_id
            """, nativeQuery = true)
    List<Object[]> saldosPorCuentaHasta(@Param("corte") LocalDate corte);
}
