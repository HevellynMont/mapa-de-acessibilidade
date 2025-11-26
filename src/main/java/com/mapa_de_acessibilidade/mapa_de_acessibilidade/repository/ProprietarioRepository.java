package com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Proprietario;

public interface ProprietarioRepository extends JpaRepository<Proprietario, Long> {
    boolean existsByEmail(String email);
}