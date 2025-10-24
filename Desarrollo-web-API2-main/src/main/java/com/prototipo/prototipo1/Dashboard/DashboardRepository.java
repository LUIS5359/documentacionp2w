package com.prototipo.prototipo1.Dashboard;

import com.prototipo.prototipo1.LibroDiario.LibroDiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface DashboardRepository extends JpaRepository<LibroDiario, Integer> {

        // --- ADMIN KPIs ---

        @Query(value = """
                        SELECT COALESCE(SUM(ld.credito), 0)::numeric
                        FROM libro_diario ld
                        JOIN cuentas c ON c.cuenta_id = ld.cuenta_id
                        WHERE c.codigo_cuenta = '4001'
                          AND date_trunc('month', ld.fecha::timestamp) = date_trunc('month', CURRENT_DATE::timestamp)
                        """, nativeQuery = true)
        BigDecimal ventasMes();

        @Query(value = """
                        SELECT COALESCE(SUM(ld.debito), 0)::numeric
                        FROM libro_diario ld
                        WHERE ld.tipo_operacion = 'Gasto'
                          AND date_trunc('month', ld.fecha::timestamp) = date_trunc('month', CURRENT_DATE::timestamp)
                        """, nativeQuery = true)
        BigDecimal gastosMes();

        @Query(value = """
                        SELECT COALESCE(SUM(i.cantidad_existente * i.precio_unitario), 0)::numeric
                        FROM inventario i
                        """, nativeQuery = true)
        BigDecimal valorInventario();

        @Query(value = """
                        SELECT COALESCE(SUM(m.debito - m.credito), 0)::numeric
                        FROM libro_mayor m
                        JOIN cuentas c ON c.cuenta_id = m.cuenta_id
                        WHERE c.codigo_cuenta = :codigo
                        """, nativeQuery = true)
        BigDecimal saldoPorCodigo(String codigo);

        // --- CONTADOR ---

        @Query(value = "SELECT COUNT(*) FROM libro_diario WHERE fecha = :hoy", nativeQuery = true)
        Integer asientosDia(LocalDate hoy);

        @Query(value = """
                        SELECT ABS(COALESCE(SUM(debito), 0) - COALESCE(SUM(credito), 0))::numeric
                        FROM libro_diario
                        WHERE fecha = :hoy
                        """, nativeQuery = true)
        BigDecimal descuadreDia(LocalDate hoy);

        interface UltimoAsiento {
                Integer getTransaccionId();

                LocalDate getFecha();

                String getDescripcion();

                BigDecimal getDebito();

                BigDecimal getCredito();
        }

        @Query(value = """
                        SELECT
                          transaccion_id AS transaccionId,
                          fecha,
                          descripcion,
                          debito::numeric AS debito,
                          credito::numeric AS credito
                        FROM libro_diario
                        ORDER BY transaccion_id DESC
                        LIMIT 10
                        """, nativeQuery = true)
        List<UltimoAsiento> ultimosAsientos();

        // --- CAJERO ---

        @Query(value = """
                        SELECT COALESCE(SUM(monto), 0)::numeric
                        FROM bancos_caja
                        WHERE fecha = :hoy AND tipo_transaccion = 'Cobro'
                        """, nativeQuery = true)
        BigDecimal cobrosHoy(LocalDate hoy);

        @Query(value = """
                        SELECT COALESCE(SUM(monto), 0)::numeric
                        FROM bancos_caja
                        WHERE fecha = :hoy AND tipo_transaccion = 'Pago'
                        """, nativeQuery = true)
        BigDecimal pagosHoy(LocalDate hoy);

        interface TopSaldo {
                String getNombre();

                BigDecimal getSaldoPendiente();
        }

        @Query(value = """
                        SELECT nombre_cliente AS nombre, saldo_pendiente::numeric AS saldoPendiente
                        FROM clientes
                        ORDER BY saldo_pendiente DESC
                        LIMIT 5
                        """, nativeQuery = true)
        List<TopSaldo> topPorCobrar();

        @Query(value = """
                        SELECT nombre_proveedor AS nombre, saldo_pendiente::numeric AS saldoPendiente
                        FROM proveedores
                        ORDER BY saldo_pendiente DESC
                        LIMIT 5
                        """, nativeQuery = true)
        List<TopSaldo> topPorPagar();
}
