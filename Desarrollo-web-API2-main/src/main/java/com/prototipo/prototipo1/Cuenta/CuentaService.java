package com.prototipo.prototipo1.Cuenta;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRepository cuentaRepository;

    public List<Cuenta> getAllCuentas() {
        return cuentaRepository.findAll();
    }

    public Cuenta getCuentaById(Integer cuentaId) {
        return cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con ID: " + cuentaId));
    }

    public Cuenta getByCodigo(String codigoCuenta) {
        return cuentaRepository.findByCodigoCuenta(codigoCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con código: " + codigoCuenta));
    }

    @Transactional
    public Cuenta createCuenta(Cuenta cuenta) {
        if (cuenta.getCodigoCuenta() == null || cuenta.getCodigoCuenta().isBlank()) {
            throw new IllegalArgumentException("El código de cuenta es obligatorio.");
        }
        if (cuentaRepository.existsByCodigoCuenta(cuenta.getCodigoCuenta())) {
            throw new RuntimeException("Ya existe una cuenta con código: " + cuenta.getCodigoCuenta());
        }
        return cuentaRepository.save(cuenta);
    }

    @Transactional
    public Cuenta updateCuenta(Integer cuentaId, Cuenta detalles) {
        Cuenta existente = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con ID: " + cuentaId));

        // Si cambian el código, valida unicidad
        if (detalles.getCodigoCuenta() != null &&
                !detalles.getCodigoCuenta().equals(existente.getCodigoCuenta()) &&
                cuentaRepository.existsByCodigoCuenta(detalles.getCodigoCuenta())) {
            throw new RuntimeException("Ya existe una cuenta con código: " + detalles.getCodigoCuenta());
        }

        existente.setCodigoCuenta(detalles.getCodigoCuenta());
        existente.setNombreCuenta(detalles.getNombreCuenta());
        existente.setTipoCuenta(detalles.getTipoCuenta());
        existente.setDescripcion(detalles.getDescripcion());

        return cuentaRepository.save(existente);
    }

    @Transactional
    public void deleteCuenta(Integer cuentaId) {
        if (!cuentaRepository.existsById(cuentaId)) {
            throw new RuntimeException("No existe una cuenta con el ID: " + cuentaId);
        }
        cuentaRepository.deleteById(cuentaId);
    }
}
