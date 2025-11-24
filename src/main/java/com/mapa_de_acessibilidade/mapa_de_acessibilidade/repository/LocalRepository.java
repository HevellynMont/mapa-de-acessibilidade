package com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// A anotação @Repository é usada para que o Spring gerencie essa interface
@Repository
// O JpaRepository recebe dois parâmetros:
// 1. A Entidade que ele gerencia (Local)
// 2. O Tipo do ID da Entidade (Long, que é o tipo do 'id' em Local.java)
public interface LocalRepository extends JpaRepository<Local, Long> {

    // Aqui podemos definir métodos customizados de busca, se necessário, 
    // como buscar por coordenadas geográficas ou por nome/descrição.

    // Exemplo de Query customizada (para busca no mapa de acessibilidade):
    // List<Local> findByLatitudeBetweenAndLongitudeBetween(
    //     double latMin, double latMax, double lonMin, double lonMax
    // );
    
    // Buscar locais por proprietário
    List<Local> findByProprietarioId(Long proprietarioId);
}
