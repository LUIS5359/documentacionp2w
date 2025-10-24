package com.prototipo.prototipo1.EstadoResultado;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadoResultadoService {

    private final EstadoResultadoRepository repository;

    public List<EstadoResultado> getAll() {
        return repository.findAll();
    }

    public EstadoResultado getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("EstadoResultado no encontrado: " + id));
    }

    public EstadoResultado create(EstadoResultado er) {
        // resultado_neto es generado por la BD
        return repository.save(er);
    }

    public EstadoResultado update(Integer id, EstadoResultado data) {
        EstadoResultado original = getById(id);
        original.setFechaInicio(data.getFechaInicio());
        original.setFechaFin(data.getFechaFin());
        original.setIngresosTotales(data.getIngresosTotales());
        original.setCostosTotales(data.getCostosTotales());
        original.setGastosTotales(data.getGastosTotales());
        // resultadoNeto lo calcula la BD; no se asigna aquí.
        return repository.save(original);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    /**
     * Calcula desde el Libro Mayor y persiste un nuevo Estado de Resultados para el
     * periodo.
     */
    @Transactional
    public EstadoResultado generarYGuardar(LocalDate inicio, LocalDate fin) {
        BigDecimal ingresos = repository.sumIngresos(inicio, fin);
        BigDecimal costos = repository.sumCostos(inicio, fin);
        BigDecimal gastos = repository.sumGastos(inicio, fin);

        EstadoResultado er = new EstadoResultado();
        er.setFechaInicio(inicio);
        er.setFechaFin(fin);
        er.setIngresosTotales(ingresos);
        er.setCostosTotales(costos);
        er.setGastosTotales(gastos);

        return repository.save(er);
    }
}
