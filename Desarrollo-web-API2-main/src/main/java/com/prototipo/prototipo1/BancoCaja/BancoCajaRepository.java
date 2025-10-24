package com.prototipo.prototipo1.BancoCaja;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BancoCajaRepository extends JpaRepository<BancoCaja, Integer> {

    List<BancoCaja> findByBanco_BancoId(Integer bancoId);

    List<BancoCaja> findByTipoTransaccionIgnoreCase(String tipoTransaccion);

    List<BancoCaja> findByFechaBetween(LocalDate desde, LocalDate hasta);

    @Query(value = """
            SELECT COALESCE(SUM(monto), 0)
            FROM bancos_caja
            WHERE (:bancoId IS NULL OR banco_id = :bancoId)
              AND (:desde IS NULL OR fecha >= :desde)
              AND (:hasta IS NULL OR fecha <= :hasta)
            """, nativeQuery = true)
    BigDecimal sumTotal(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            @Param("bancoId") Integer bancoId);

    @Query(value = """
            SELECT LOWER(tipo_transaccion) AS tipo, COALESCE(SUM(monto),0) AS total
            FROM bancos_caja
            WHERE (:bancoId IS NULL OR banco_id = :bancoId)
              AND (:desde IS NULL OR fecha >= :desde)
              AND (:hasta IS NULL OR fecha <= :hasta)
            GROUP BY LOWER(tipo_transaccion)
            """, nativeQuery = true)
    List<Object[]> sumPorTipo(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            @Param("bancoId") Integer bancoId);
}
