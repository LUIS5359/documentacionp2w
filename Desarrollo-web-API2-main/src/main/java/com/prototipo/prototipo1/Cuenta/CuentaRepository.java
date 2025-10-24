package com.prototipo.prototipo1.Cuenta;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {
    boolean existsByCodigoCuenta(String codigoCuenta);

    Optional<Cuenta> findByCodigoCuenta(String codigoCuenta);
}
