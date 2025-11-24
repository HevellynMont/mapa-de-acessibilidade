package com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.TagAcessibilidade;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório para a entidade TagAcessibilidade.
 * Responsável por todas as operações de acesso a dados (CRUD) 
 * da tabela 'tag_acessibilidade' no MySQL/Aiven.
 */
@Repository
public interface TagAcessibilidadeRepository extends JpaRepository<TagAcessibilidade, Long> {

    
    // Opcional: Adicionar um método de busca por nome, que pode ser útil 
    // para administradores ou para o Front-end buscar tags específicas.
    Optional<TagAcessibilidade> findByNome(String nome);
}