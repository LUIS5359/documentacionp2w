package com.prototipo.prototipo1.BalanceSaldo;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.prototipo.prototipo1.Cuenta.Cuenta;

public interface BalanceSaldoRepository extends JpaRepository<BalanceSaldo, Integer> {

    Optional<BalanceSaldo> findFirstByCuentaOrderByFechaDesc(Cuenta cuenta);

    @Modifying
    void deleteByFecha(LocalDate fecha);
}