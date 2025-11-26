package com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByLocalId(Long idLocal);
}