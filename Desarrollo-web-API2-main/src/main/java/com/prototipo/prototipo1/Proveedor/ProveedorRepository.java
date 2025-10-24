package com.prototipo.prototipo1.Proveedor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    List<Proveedor> findByNombreProveedorContainingIgnoreCase(String nombre);

    Optional<Proveedor> findByCorreoIgnoreCase(String correo);

    List<Proveedor> findBySaldoPendienteBetween(BigDecimal min, BigDecimal max);

    boolean existsByCorreoIgnoreCase(String correo);
}
