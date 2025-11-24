package com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.ComentarioUsuario;

@Repository
public interface ComentarioRepository extends JpaRepository<ComentarioUsuario, Long>{
	//ele vai achar os comentarios de um local, ordenados por data de criação
	List<ComentarioUsuario> findByLocalIdOrderByDataCriacaoDesc(Long localId);
	
	//ele vai achar os comentarios de um usuario, ordenados por data de criação
	List<ComentarioUsuario> findByPessoaIdOrderByDataCriacaoDesc(Long pessoaId);
	
	// Query com JOIN FETCH para garantir que o Local seja carregado
	@Query("SELECT c FROM ComentarioUsuario c " +
	       "LEFT JOIN FETCH c.local " +
	       "LEFT JOIN FETCH c.tagsComentadas " +
	       "WHERE c.pessoa.id = :usuarioId " +
	       "ORDER BY c.dataCriacao DESC")
	List<ComentarioUsuario> findByUsuarioComLocal(@Param("usuarioId") Long usuarioId);
	
}
