package com.prototipo.prototipo1.Cliente;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    List<Cliente> findByNombreClienteContainingIgnoreCase(String q);

    Optional<Cliente> findByCorreoIgnoreCase(String correo);

    List<Cliente> findBySaldoPendienteBetween(BigDecimal min, BigDecimal max);

    boolean existsByCorreoIgnoreCase(String correo);
}
