package com.prototipo.prototipo1.Cliente;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;

    public List<Cliente> getAll() {
        return repository.findAll();
    }

    public Cliente getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));
    }

    public List<Cliente> buscarPorNombre(String q) {
        return repository.findByNombreClienteContainingIgnoreCase(q);
    }

    public Cliente buscarPorCorreo(String correo) {
        return repository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new IllegalArgumentException("No existe cliente con correo: " + correo));
    }

    public List<Cliente> buscarPorRangoSaldo(BigDecimal min, BigDecimal max) {
        if (min == null)
            min = BigDecimal.ZERO;
        if (max == null)
            max = new BigDecimal("999999999999.99");
        return repository.findBySaldoPendienteBetween(min, max);
    }

    public Cliente create(Cliente c) {
        validar(c, true);
        normalizar(c);
        if (c.getSaldoPendiente() == null)
            c.setSaldoPendiente(BigDecimal.ZERO);
        return repository.save(c);
    }

    public Cliente update(Integer id, Cliente data) {
        Cliente c = getById(id);
        c.setNombreCliente(data.getNombreCliente());
        c.setDireccion(data.getDireccion());
        c.setTelefono(data.getTelefono());
        c.setCorreo(data.getCorreo());
        c.setSaldoPendiente(
                data.getSaldoPendiente() == null ? c.getSaldoPendiente() : data.getSaldoPendiente());
        validar(c, false);
        normalizar(c);
        return repository.save(c);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    /** Incrementa saldo (p. ej., nueva venta a crédito). */
    public Cliente cargarSaldo(Integer id, BigDecimal monto) {
        if (monto == null || monto.signum() <= 0)
            throw new IllegalArgumentException("El monto a cargar debe ser > 0.");
        Cliente c = getById(id);
        c.setSaldoPendiente(c.getSaldoPendiente().add(monto));
        return repository.save(c);
    }

    /**
     * Disminuye saldo (p. ej., abono/pago del cliente). No permite quedar negativo.
     */
    public Cliente abonarSaldo(Integer id, BigDecimal monto) {
        if (monto == null || monto.signum() <= 0)
            throw new IllegalArgumentException("El monto a abonar debe ser > 0.");
        Cliente c = getById(id);
        BigDecimal nuevo = c.getSaldoPendiente().subtract(monto);
        if (nuevo.signum() < 0)
            throw new IllegalArgumentException("El abono excede el saldo pendiente.");
        c.setSaldoPendiente(nuevo);
        return repository.save(c);
    }

    /** Fija el saldo a un valor exacto (usarlo con cuidado). */
    public Cliente actualizarSaldo(Integer id, BigDecimal nuevoSaldo) {
        if (nuevoSaldo == null || nuevoSaldo.signum() < 0)
            throw new IllegalArgumentException("El saldo debe ser >= 0.");
        Cliente c = getById(id);
        c.setSaldoPendiente(nuevoSaldo);
        return repository.save(c);
    }

    private void validar(Cliente c, boolean checkCorreoDuplicado) {
        if (c.getNombreCliente() == null || c.getNombreCliente().isBlank())
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        if (c.getSaldoPendiente() != null && c.getSaldoPendiente().signum() < 0)
            throw new IllegalArgumentException("El saldo pendiente no puede ser negativo.");
        if (c.getCorreo() != null && !c.getCorreo().isBlank()) {
            // validación simple de formato
            if (!c.getCorreo().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
                throw new IllegalArgumentException("Correo con formato inválido.");
            if (checkCorreoDuplicado && repository.existsByCorreoIgnoreCase(c.getCorreo())) {
                throw new IllegalArgumentException("Ya existe un cliente con ese correo.");
            }
        }
    }

    private void normalizar(Cliente c) {
        if (c.getCorreo() != null)
            c.setCorreo(c.getCorreo().trim());
        if (c.getNombreCliente() != null)
            c.setNombreCliente(c.getNombreCliente().trim());
        if (c.getTelefono() != null)
            c.setTelefono(c.getTelefono().trim());
    }
}
