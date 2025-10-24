package com.prototipo.prototipo1.Dashboard.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

        // ADMIN
        @Builder.Default
        private BigDecimal ventasMes = BigDecimal.ZERO;
        @Builder.Default
        private BigDecimal gastosMes = BigDecimal.ZERO;
        @Builder.Default
        private BigDecimal valorInventario = BigDecimal.ZERO;
        @Builder.Default
        private BigDecimal saldoBanco = BigDecimal.ZERO;

        // CONTADOR
        @Builder.Default
        private Integer asientosDia = 0;
        @Builder.Default
        private BigDecimal descuadreDia = BigDecimal.ZERO;
        @Builder.Default
        private List<AsientoItem> ultimosAsientos = List.of();

        // CAJERO
        @Builder.Default
        private BigDecimal cobrosHoy = BigDecimal.ZERO;
        @Builder.Default
        private BigDecimal pagosHoy = BigDecimal.ZERO;
        @Builder.Default
        private List<TopItem> topPorCobrar = List.of();
        @Builder.Default
        private List<TopItem> topPorPagar = List.of();

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class AsientoItem {
                private Integer transaccionId;
                private LocalDate fecha;
                private String descripcion;
                private BigDecimal debito;
                private BigDecimal credito;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TopItem {
                private String nombre;
                private BigDecimal saldoPendiente;
        }
}
