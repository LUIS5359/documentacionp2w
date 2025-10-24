package com.prototipo.prototipo1.Banco;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BancoRepository extends JpaRepository<Banco, Integer> {
    
    List<Banco> findByActivoTrue();
    
    Banco findByCodigo(String codigo);
}