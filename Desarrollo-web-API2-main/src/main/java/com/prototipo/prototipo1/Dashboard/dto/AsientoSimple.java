package com.prototipo.prototipo1.Dashboard.dto;

import java.time.LocalDate;

public record AsientoSimple(
        Integer id,
        LocalDate fecha,
        String descripcion,
        Double debito,
        Double credito) {
}
