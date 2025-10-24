package com.prototipo.prototipo1.Banco;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BancoService {

    private final BancoRepository repository;

    public List<Banco> getAll() {
        return repository.findAll();
    }

    public List<Banco> getAllActivos() {
        return repository.findByActivoTrue();
    }

    public Banco getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Banco no encontrado: " + id));
    }

    public Banco create(Banco banco) {
        validar(banco);
        return repository.save(banco);
    }

    public Banco update(Integer id, Banco data) {
        Banco banco = getById(id);
        banco.setNombre(data.getNombre());
        banco.setCodigo(data.getCodigo());
        banco.setDescripcion(data.getDescripcion());
        banco.setActivo(data.getActivo());
        validar(banco);
        return repository.save(banco);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    private void validar(Banco banco) {
        if (banco.getNombre() == null || banco.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del banco es obligatorio.");
        }
    }
}