package com.prototipo.prototipo1.Inventario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "inventario")
@AllArgsConstructor
@NoArgsConstructor
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id")
    private Integer productoId;

    @Column(name = "nombre_producto", length = 100, nullable = false)
    private String nombreProducto;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "cantidad_existente", nullable = false)
    private Integer cantidadExistente;

    @Column(name = "precio_unitario", precision = 15, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    @Column(name = "tipo_producto", length = 50, nullable = false)
    private String tipoProducto; // 'Mercadería', 'Mobiliario', 'Equipo'
}
