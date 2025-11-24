package com.mapa_de_acessibilidade.mapa_de_acessibilidade.model;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tag_acessibilidade")
public class TagAcessibilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome; // Ex: "Rampa Acessível", "Banheiro Adaptado"

    // [OPCIONAL] Para o Front-end exibir o ícone
    private String iconeUrl; 
    
    // Construtor sem argumentos é obrigatório para JPA
    public TagAcessibilidade() {}

    public TagAcessibilidade(Long id, String nome, String iconeUrl) {
        this.id = id;
        this.nome = nome;
        this.iconeUrl = iconeUrl;
    }



    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIconeUrl() {
        return iconeUrl;
    }
    public void setIconeUrl(String iconeUrl) {
        this.iconeUrl = iconeUrl;
    }



  
}