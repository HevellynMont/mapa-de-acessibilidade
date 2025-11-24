package com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Pessoa;


@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    Optional<Pessoa> findByEmail(String email);

    Optional<Pessoa> findByLogin(String login);

    boolean existsByEmail(String email);

    boolean existsByLogin(String login);

}
