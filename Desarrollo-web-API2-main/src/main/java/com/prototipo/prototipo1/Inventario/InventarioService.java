package com.prototipo.prototipo1.Inventario;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioRepository repository;

    public List<Inventario> getAll() {
        return repository.findAll();
    }

    public Inventario getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
    }

    public List<Inventario> buscarPorNombre(String q) {
        return repository.findByNombreProductoContainingIgnoreCase(q);
    }

    public List<Inventario> buscarPorTipo(String tipo) {
        return repository.findByTipoProductoIgnoreCase(tipo);
    }

    public Inventario create(Inventario p) {
        validarProducto(p);
        return repository.save(p);
    }

    public Inventario update(Integer id, Inventario data) {
        Inventario p = getById(id);
        p.setNombreProducto(data.getNombreProducto());
        p.setDescripcion(data.getDescripcion());
        p.setCantidadExistente(data.getCantidadExistente());
        p.setPrecioUnitario(data.getPrecioUnitario());
        p.setTipoProducto(data.getTipoProducto());
        validarProducto(p);
        return repository.save(p);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    /**
     * Ajusta stock positivo/negativo (ingreso/egreso). No permite quedar negativo.
     */
    public Inventario ajustarStock(Integer id, Integer delta) {
        Inventario p = getById(id);
        int nuevoStock = p.getCantidadExistente() + delta;
        if (nuevoStock < 0)
            throw new IllegalArgumentException("Stock insuficiente para egreso.");
        p.setCantidadExistente(nuevoStock);
        return repository.save(p);
    }

    /** Actualiza el precio unitario. */
    public Inventario actualizarPrecio(Integer id, BigDecimal nuevoPrecio) {
        if (nuevoPrecio == null || nuevoPrecio.signum() < 0)
            throw new IllegalArgumentException("El precio debe ser >= 0.");
        Inventario p = getById(id);
        p.setPrecioUnitario(nuevoPrecio);
        return repository.save(p);
    }

    private void validarProducto(Inventario p) {
        if (p.getNombreProducto() == null || p.getNombreProducto().isBlank())
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        if (p.getCantidadExistente() == null || p.getCantidadExistente() < 0)
            throw new IllegalArgumentException("La cantidad existente debe ser >= 0.");
        if (p.getPrecioUnitario() == null || p.getPrecioUnitario().signum() < 0)
            throw new IllegalArgumentException("El precio unitario debe ser >= 0.");
        if (p.getTipoProducto() == null || p.getTipoProducto().isBlank())
            throw new IllegalArgumentException("El tipo de producto es obligatorio.");
    }
}
