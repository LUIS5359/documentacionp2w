package com.prototipo.prototipo1.LibroMayor;

import com.prototipo.prototipo1.BalanceSaldo.BalanceSaldo;
import com.prototipo.prototipo1.BalanceSaldo.BalanceSaldoRepository;
import com.prototipo.prototipo1.Cuenta.Cuenta;
import com.prototipo.prototipo1.Cuenta.CuentaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LibroMayorService {

    private final LibroMayorRepository libroMayorRepository;
    private final BalanceSaldoRepository balanceSaldoRepository;
    private final CuentaRepository cuentaRepository;

    /**
     * Retorna todos los movimientos registrados en el Libro Mayor.
     */
    public List<LibroMayor> getAllLibroMayor() {
        return libroMayorRepository.findAll();
    }

    /**
     * Retorna todos los movimientos del Libro Mayor para una cuenta específica.
     */
    public List<LibroMayor> getLibroMayorByCuenta(Cuenta cuentaId) {
        return libroMayorRepository.findByCuenta(cuentaId);
    }

    /**
     * Calcula el saldo total (débitos - créditos) de una cuenta específica.
     */
    public BigDecimal getSaldoTotalByCuenta(Cuenta cuentaId) {
        List<LibroMayor> movimientos = libroMayorRepository.findByCuenta(cuentaId);

        BigDecimal totalDebito = movimientos.stream()
                .map(LibroMayor::getDebito)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredito = movimientos.stream()
                .map(LibroMayor::getCredito)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalDebito.subtract(totalCredito);
    }

    /**
     * Crea un registro en Libro Mayor a partir de un movimiento del Libro Diario.
     * (Se llama automáticamente desde LibroDiarioService)
     */
    @Transactional
    public LibroMayor registrarDesdeDiario(com.prototipo.prototipo1.LibroDiario.LibroDiario mov) {
        LibroMayor mayor = new LibroMayor();
        mayor.setCuenta(mov.getCuenta());
        mayor.setTransaccion(mov);
        mayor.setFecha(mov.getFecha());
        mayor.setDebito(mov.getDebito());
        mayor.setCredito(mov.getCredito());
        return libroMayorRepository.save(mayor);
    }

    @Transactional
    public List<BalanceSaldo> generarBalanceViaSQL(LocalDate fechaCorte) {
        balanceSaldoRepository.deleteByFecha(fechaCorte);

        List<Object[]> filas = libroMayorRepository.saldosPorCuentaHasta(fechaCorte);
        Map<Integer, BigDecimal> map = new HashMap<>();
        for (Object[] f : filas) {
            Integer cuentaId = ((Number) f[0]).intValue();
            BigDecimal saldo = (BigDecimal) f[1];
            map.put(cuentaId, saldo);
        }

        List<BalanceSaldo> balances = new ArrayList<>();
        for (Cuenta cuenta : cuentaRepository.findAll()) {
            BigDecimal saldo = map.getOrDefault(cuenta.getCuentaId(), BigDecimal.ZERO);

            BalanceSaldo b = new BalanceSaldo();
            b.setCuenta(cuenta);
            b.setFecha(fechaCorte);
            if (saldo.signum() > 0) {
                b.setSaldoDeudor(saldo);
                b.setSaldoAcreedor(BigDecimal.ZERO);
            } else if (saldo.signum() < 0) {
                b.setSaldoDeudor(BigDecimal.ZERO);
                b.setSaldoAcreedor(saldo.abs());
            } else {
                b.setSaldoDeudor(BigDecimal.ZERO);
                b.setSaldoAcreedor(BigDecimal.ZERO);
            }
            balances.add(b);
        }

        return balanceSaldoRepository.saveAll(balances);
    }
}
