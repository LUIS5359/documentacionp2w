package com.prototipo.prototipo1.Auth;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Integer rolId;

    @Column(name = "nombre_rol", length = 50, nullable = false)
    private String nombreRol; // 'Administrador', 'Contador', 'Cajero'
}
