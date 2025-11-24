package com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Usuario;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

   
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.comentarios c WHERE c.local.id = :localId")
    List<Usuario> findUsuariosByLocalId(@Param("localId") Long localId);

    List<Usuario> findByNomeContainingIgnoreCase(String nome);
    
    // Métodos para autenticação
    Optional<Usuario> findByEmail(String email);
    
    Optional<Usuario> findByEmailAndSenha(String email, Integer senha);

}
