package com.prototipo.prototipo1.BancoCaja;

import com.prototipo.prototipo1.Banco.Banco;
import com.prototipo.prototipo1.Banco.BancoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BancoCajaService {

    private final BancoCajaRepository repository;
    private final BancoRepository bancoRepository;  // NUEVO

    // --- CRUD ---
    public List<BancoCajaDTO> getAll() {
        return repository.findAll().stream()
                .map(BancoCajaDTO::new)
                .collect(Collectors.toList());
    }

    public BancoCajaDTO getById(Integer id) {
        BancoCaja entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada: " + id));
        return new BancoCajaDTO(entity);
    }

    public BancoCajaDTO create(BancoCajaDTO dto) {
        validarDTO(dto);
        
        // Buscar el banco
        Banco banco = bancoRepository.findById(dto.getBancoId())
                .orElseThrow(() -> new IllegalArgumentException("Banco no encontrado: " + dto.getBancoId()));
        
        // Convertir DTO a entidad
        BancoCaja entity = dto.toEntity(banco);
        
        validarEntidad(entity);
        normalizar(entity);
        
        BancoCaja saved = repository.save(entity);
        return new BancoCajaDTO(saved);
    }

    public BancoCajaDTO update(Integer id, BancoCajaDTO dto) {
        BancoCaja entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada: " + id));
        
        validarDTO(dto);
        
        // Buscar el banco (puede ser diferente)
        Banco banco = bancoRepository.findById(dto.getBancoId())
                .orElseThrow(() -> new IllegalArgumentException("Banco no encontrado: " + dto.getBancoId()));
        
        // Actualizar campos
        entity.setFecha(dto.getFecha());
        entity.setTipoTransaccion(dto.getTipoTransaccion());
        entity.setMonto(dto.getMonto());
        entity.setBanco(banco);
        entity.setDescripcion(dto.getDescripcion());
        
        validarEntidad(entity);
        normalizar(entity);
        
        BancoCaja saved = repository.save(entity);
        return new BancoCajaDTO(saved);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    // --- Búsquedas / Filtros ---
    public List<BancoCajaDTO> buscar(Integer bancoId, String tipo, LocalDate desde, LocalDate hasta) {
        List<BancoCaja> base = repository.findAll();
        return base.stream()
                .filter(t -> bancoId == null || Objects.equals(t.getBanco().getBancoId(), bancoId))
                .filter(t -> tipo == null || t.getTipoTransaccion().equalsIgnoreCase(tipo))
                .filter(t -> (desde == null || !t.getFecha().isBefore(desde)) &&
                        (hasta == null || !t.getFecha().isAfter(hasta)))
                .map(BancoCajaDTO::new)
                .toList();
    }

    // --- Resúmenes ---
    public BigDecimal total(LocalDate desde, LocalDate hasta, Integer bancoId) {
        return repository.sumTotal(desde, hasta, bancoId);
    }

    public Map<String, BigDecimal> totalPorTipo(LocalDate desde, LocalDate hasta, Integer bancoId) {
        List<Object[]> filas = repository.sumPorTipo(desde, hasta, bancoId);
        Map<String, BigDecimal> out = new LinkedHashMap<>();
        for (Object[] f : filas) {
            String tipo = (String) f[0];
            BigDecimal total = (BigDecimal) f[1];
            out.put(tipo, total);
        }
        return out;
    }

    // --- Validaciones ---
    private void validarDTO(BancoCajaDTO dto) {
        if (dto.getFecha() == null)
            throw new IllegalArgumentException("La fecha es obligatoria.");
        if (dto.getTipoTransaccion() == null || dto.getTipoTransaccion().isBlank())
            throw new IllegalArgumentException("El tipo de transacción es obligatorio.");
        if (dto.getMonto() == null || dto.getMonto().signum() < 0)
            throw new IllegalArgumentException("El monto debe ser >= 0.");
        if (dto.getBancoId() == null)
            throw new IllegalArgumentException("El banco es obligatorio.");
    }

    private void validarEntidad(BancoCaja t) {
        Set<String> permitidos = Set.of("depósito", "cheque emitido", "pago", "cobro");
        if (!permitidos.contains(t.getTipoTransaccion().toLowerCase())) {
            throw new IllegalArgumentException("Tipo de transacción no válido. Permitidos: " + permitidos);
        }
    }

    private void normalizar(BancoCaja t) {
        t.setTipoTransaccion(t.getTipoTransaccion().trim());
        if (t.getDescripcion() != null)
            t.setDescripcion(t.getDescripcion().trim());
    }
}