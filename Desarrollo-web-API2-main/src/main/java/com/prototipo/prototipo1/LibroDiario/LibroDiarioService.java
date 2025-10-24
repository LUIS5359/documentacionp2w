package com.prototipo.prototipo1.LibroDiario;

import com.prototipo.prototipo1.LibroMayor.LibroMayorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibroDiarioService {

    private final LibroDiarioRepository libroDiarioRepository;
    private final LibroMayorService libroMayorService; // <- si aún no lo tienes, quita esta línea y la llamada

    public List<LibroDiario> getAllLibroDiarios() {
        return libroDiarioRepository.findAll();
    }

    public LibroDiario getLibroDiarioId(Integer transaccionId) {
        return libroDiarioRepository.findById(transaccionId)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada con ID: " + transaccionId));
    }

    @Transactional
    public LibroDiario createLibroDiario(LibroDiario libroDiario) {
        // Validaciones básicas (opcional)
        if (libroDiario.getDebito() == null || libroDiario.getCredito() == null) {
            throw new IllegalArgumentException("Los valores de débito y crédito no pueden ser nulos.");
        }

        LibroDiario guardado = libroDiarioRepository.save(libroDiario);

        // Traslado automático al mayor (si tienes el servicio)
        libroMayorService.registrarDesdeDiario(guardado);

        return guardado;
    }

    @Transactional
    public LibroDiario updateLibroDiario(Integer transaccionId, LibroDiario detalles) {
        LibroDiario existente = libroDiarioRepository.findById(transaccionId)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada con ID: " + transaccionId));

        // OJO: estos nombres deben coincidir con tu entidad "pro" que te propuse:
        existente.setCuenta(detalles.getCuenta()); // relación @ManyToOne
        existente.setFecha(detalles.getFecha()); // LocalDate
        existente.setDescripcion(detalles.getDescripcion());
        existente.setTipo_operacion(detalles.getTipo_operacion()); // tomar del parámetro, no del existente
        existente.setDebito(detalles.getDebito()); // BigDecimal
        existente.setCredito(detalles.getCredito()); // BigDecimal
        existente.setDocumentoRespaldo(detalles.getDocumentoRespaldo()); // camelCase

        LibroDiario actualizado = libroDiarioRepository.save(existente);

        // Si quieres reflejar cambios en el mayor, puedes (estrategia: borrar y
        // re-crear, o actualizar uno a uno).
        // libroMayorService.actualizarDesdeDiario(actualizado); // <- si implementas
        // algo así.

        return actualizado;
    }

    public void deleteLibroDiario(Integer transaccionId) {
        if (!libroDiarioRepository.existsById(transaccionId)) {
            throw new RuntimeException("No existe una transacción con el ID: " + transaccionId);
        }
        libroDiarioRepository.deleteById(transaccionId);
    }
}
