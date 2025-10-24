package com.prototipo.prototipo1.BalanceSaldo;

import com.prototipo.prototipo1.Cuenta.Cuenta;
import com.prototipo.prototipo1.Cuenta.CuentaRepository;
import com.prototipo.prototipo1.LibroMayor.LibroMayorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceSaldoService {

    private final BalanceSaldoRepository balanceSaldoRepository;
    private final CuentaRepository cuentaRepository;
    private final LibroMayorService libroMayorService;

    /** Genera balance para TODAS las cuentas con fecha de corte específica. */
    @Transactional
    public List<BalanceSaldo> generarBalance(LocalDate fechaCorte) {
        // 1) (Opcional) evitar duplicados del mismo día: borrar existentes del día
        balanceSaldoRepository.deleteByFecha(fechaCorte);

        List<BalanceSaldo> balances = new ArrayList<>();
        List<Cuenta> cuentas = cuentaRepository.findAll();

        for (Cuenta cuenta : cuentas) {
            // Si tu LibroMayorService soporta fecha de corte, úsala.
            // Si no, usa tu método actual (suma de todo historial).
            BigDecimal saldo = libroMayorService.getSaldoTotalByCuenta(cuenta);

            BalanceSaldo balance = new BalanceSaldo();
            balance.setCuenta(cuenta);
            balance.setFecha(fechaCorte);

            if (saldo.compareTo(BigDecimal.ZERO) > 0) {
                balance.setSaldoDeudor(saldo);
                balance.setSaldoAcreedor(BigDecimal.ZERO);
            } else if (saldo.compareTo(BigDecimal.ZERO) < 0) {
                balance.setSaldoDeudor(BigDecimal.ZERO);
                balance.setSaldoAcreedor(saldo.abs());
            } else {
                balance.setSaldoDeudor(BigDecimal.ZERO);
                balance.setSaldoAcreedor(BigDecimal.ZERO);
            }

            balances.add(balance);
        }

        // 2) Batch save
        return balanceSaldoRepository.saveAll(balances);
    }

    /** Versión por defecto: usa hoy como fecha de corte. */
    @Transactional
    public List<BalanceSaldo> generarBalanceGeneral() {
        return generarBalance(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<BalanceSaldo> getAllBalances() {
        return balanceSaldoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public BalanceSaldo getBalanceByCuenta(Cuenta cuenta) {
        return balanceSaldoRepository.findFirstByCuentaOrderByFechaDesc(cuenta).orElse(null);
    }
    
    /** Método adicional para buscar por ID de cuenta */
    @Transactional(readOnly = true)
    public BalanceSaldo getBalanceByCuentaId(Integer cuentaId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId).orElse(null);
        if (cuenta == null) {
            return null;
        }
        return balanceSaldoRepository.findFirstByCuentaOrderByFechaDesc(cuenta).orElse(null);
    }
}