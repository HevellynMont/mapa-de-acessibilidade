package com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByEmail(String email);
}