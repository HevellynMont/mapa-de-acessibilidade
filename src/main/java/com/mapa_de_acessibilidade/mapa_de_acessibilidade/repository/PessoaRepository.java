package com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    Optional<Pessoa> findByEmail(String email);
}