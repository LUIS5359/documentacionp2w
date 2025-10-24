package com.prototipo.prototipo1.Cuenta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prototipo.prototipo1.LibroDiario.LibroDiario;
import com.prototipo.prototipo1.LibroMayor.LibroMayor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Entity
@Table(name = "cuentas")
@AllArgsConstructor
@NoArgsConstructor
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cuenta_id")
    private Integer cuentaId;

    @Column(name = "codigo_cuenta", length = 20, unique = true, nullable = false)
    private String codigoCuenta;

    @Column(name = "nombre_cuenta", length = 100, nullable = false)
    private String nombreCuenta;

    @Column(name = "tipo_cuenta", length = 50, nullable = false)
    private String tipoCuenta;
    // Ejemplo: "Activo", "Pasivo", "Patrimonio", "Ingreso", "Gasto"

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // --- Relaciones ---

    @JsonIgnore
    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LibroDiario> movimientosDiario;

    @JsonIgnore
    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LibroMayor> movimientosMayor;
}
