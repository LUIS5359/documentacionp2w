package com.prototipo.prototipo1.BancoCaja;

import com.prototipo.prototipo1.Banco.Banco;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "bancos_caja")
@AllArgsConstructor
@NoArgsConstructor
public class BancoCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaccion_banco_id")
    private Integer transaccionBancoId;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "tipo_transaccion", length = 50, nullable = false)
    private String tipoTransaccion;

    @Column(name = "monto", precision = 15, scale = 2, nullable = false)
    private BigDecimal monto;

    // ====== RELACIÓN CON BANCO ======
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "banco_id", nullable = false)
    private Banco banco;  // Objeto Banco completo
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
}