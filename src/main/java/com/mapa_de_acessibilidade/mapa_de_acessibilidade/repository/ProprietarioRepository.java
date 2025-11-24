package com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Proprietario;

@Repository
public interface ProprietarioRepository extends JpaRepository<Proprietario, Long> {

   
    @Query("SELECT DISTINCT p FROM Proprietario p JOIN p.locais l WHERE l.endereco LIKE %:cidade%")
    List<Proprietario> findProprietariosByCidade(@Param("cidade") String cidade);

    List<Proprietario> findByNomeContainingIgnoreCase(String nome);
    
    @Query("SELECT COUNT(l) FROM Local l WHERE l.proprietario.id = :proprietarioId")
    Long countLocaisByProprietarioId(@Param("proprietarioId") Long proprietarioId);
    
    // Métodos para autenticação
    Optional<Proprietario> findByEmail(String email);
    
    Optional<Proprietario> findByEmailAndSenha(String email, Integer senha);

}
