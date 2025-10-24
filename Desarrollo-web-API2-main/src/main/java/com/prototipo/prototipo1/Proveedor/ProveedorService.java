package com.prototipo.prototipo1.Proveedor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository repository;

    public List<Proveedor> getAll() {
        return repository.findAll();
    }

    public Proveedor getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado: " + id));
    }

    public List<Proveedor> buscarPorNombre(String q) {
        return repository.findByNombreProveedorContainingIgnoreCase(q);
    }

    public Proveedor buscarPorCorreo(String correo) {
        return repository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new IllegalArgumentException("No existe proveedor con correo: " + correo));
    }

    public List<Proveedor> buscarPorRangoSaldo(BigDecimal min, BigDecimal max) {
        if (min == null)
            min = BigDecimal.ZERO;
        if (max == null)
            max = new BigDecimal("999999999999.99");
        return repository.findBySaldoPendienteBetween(min, max);
    }

    public Proveedor create(Proveedor p) {
        validar(p, true);
        normalizar(p);
        if (p.getSaldoPendiente() == null)
            p.setSaldoPendiente(BigDecimal.ZERO);
        return repository.save(p);
    }

    public Proveedor update(Integer id, Proveedor data) {
        Proveedor p = getById(id);
        p.setNombreProveedor(data.getNombreProveedor());
        p.setDireccion(data.getDireccion());
        p.setTelefono(data.getTelefono());
        p.setCorreo(data.getCorreo());
        p.setSaldoPendiente(
                data.getSaldoPendiente() == null ? p.getSaldoPendiente() : data.getSaldoPendiente());
        validar(p, false);
        normalizar(p);
        return repository.save(p);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    /** Aumenta el saldo (p. ej. nueva compra a crédito). */
    public Proveedor cargarSaldo(Integer id, BigDecimal monto) {
        if (monto == null || monto.signum() <= 0)
            throw new IllegalArgumentException("El monto a cargar debe ser > 0.");
        Proveedor p = getById(id);
        p.setSaldoPendiente(p.getSaldoPendiente().add(monto));
        return repository.save(p);
    }

    /** Disminuye saldo (p. ej. pago a proveedor). */
    public Proveedor abonarSaldo(Integer id, BigDecimal monto) {
        if (monto == null || monto.signum() <= 0)
            throw new IllegalArgumentException("El monto a abonar debe ser > 0.");
        Proveedor p = getById(id);
        BigDecimal nuevo = p.getSaldoPendiente().subtract(monto);
        if (nuevo.signum() < 0)
            throw new IllegalArgumentException("El abono excede el saldo pendiente.");
        p.setSaldoPendiente(nuevo);
        return repository.save(p);
    }

    /** Fija el saldo exacto (usarlo con cuidado). */
    public Proveedor actualizarSaldo(Integer id, BigDecimal nuevoSaldo) {
        if (nuevoSaldo == null || nuevoSaldo.signum() < 0)
            throw new IllegalArgumentException("El saldo debe ser >= 0.");
        Proveedor p = getById(id);
        p.setSaldoPendiente(nuevoSaldo);
        return repository.save(p);
    }

    private void validar(Proveedor p, boolean checkCorreoDuplicado) {
        if (p.getNombreProveedor() == null || p.getNombreProveedor().isBlank())
            throw new IllegalArgumentException("El nombre del proveedor es obligatorio.");
        if (p.getSaldoPendiente() != null && p.getSaldoPendiente().signum() < 0)
            throw new IllegalArgumentException("El saldo pendiente no puede ser negativo.");
        if (p.getCorreo() != null && !p.getCorreo().isBlank()) {
            if (!p.getCorreo().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
                throw new IllegalArgumentException("Correo con formato inválido.");
            if (checkCorreoDuplicado && repository.existsByCorreoIgnoreCase(p.getCorreo())) {
                throw new IllegalArgumentException("Ya existe un proveedor con ese correo.");
            }
        }
    }

    private void normalizar(Proveedor p) {
        if (p.getCorreo() != null)
            p.setCorreo(p.getCorreo().trim());
        if (p.getNombreProveedor() != null)
            p.setNombreProveedor(p.getNombreProveedor().trim());
        if (p.getTelefono() != null)
            p.setTelefono(p.getTelefono().trim());
    }
}
