package com.prototipo.prototipo1.BancoCaja;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.prototipo.prototipo1.Banco.Banco;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BancoCajaDTO {
    private Integer transaccionBancoId;
    private LocalDate fecha;
    private String tipoTransaccion;
    private BigDecimal monto;
    private Integer bancoId;           // Para mantener compatibilidad
    private String bancoNombre;        // NUEVO: nombre del banco
    private String bancoCodigo;        // NUEVO: código del banco
    private String descripcion;

    // Constructor desde entidad
    public BancoCajaDTO(BancoCaja entity) {
        this.transaccionBancoId = entity.getTransaccionBancoId();
        this.fecha = entity.getFecha();
        this.tipoTransaccion = entity.getTipoTransaccion();
        this.monto = entity.getMonto();
        this.bancoId = entity.getBanco().getBancoId();
        this.bancoNombre = entity.getBanco().getNombre();
        this.bancoCodigo = entity.getBanco().getCodigo();
        this.descripcion = entity.getDescripcion();
    }

    // Método para convertir DTO a entidad
    public BancoCaja toEntity(Banco banco) {
        BancoCaja entity = new BancoCaja();
        entity.setTransaccionBancoId(this.transaccionBancoId);
        entity.setFecha(this.fecha);
        entity.setTipoTransaccion(this.tipoTransaccion);
        entity.setMonto(this.monto);
        entity.setBanco(banco);
        entity.setDescripcion(this.descripcion);
        return entity;
    }
}