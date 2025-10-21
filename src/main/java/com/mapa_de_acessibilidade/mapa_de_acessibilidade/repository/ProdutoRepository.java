package com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
