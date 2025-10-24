package com.prototipo.prototipo1.Inventario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventarioRepository extends JpaRepository<Inventario, Integer> {
    List<Inventario> findByNombreProductoContainingIgnoreCase(String nombre);

    List<Inventario> findByTipoProductoIgnoreCase(String tipoProducto);
}
