package com.mapa_de_acessibilidade.mapa_de_acessibilidade.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proprietario")

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Proprietario extends Pessoa {

    @OneToMany(mappedBy = "proprietario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Local> locais = new HashSet<>();

    // Getters e Setters
    public Set<Local> getLocais() {
        return locais;
    }

    public void setLocais(Set<Local> locais) {
        this.locais = locais;
    }


    public Proprietario(String nome, String email, String telefone, String login, Integer senha) {
        super(nome, email, telefone, login, senha);
        this.locais = new HashSet<>();
    }


    public void adicionarLocal(Local local) {
        locais.add(local);
        local.setProprietario(this);
    }

    public void removerLocal(Local local) {
        locais.remove(local);
        local.setProprietario(null);
    }
    
    @Override
    public void removerComentario(ComentarioUsuario comentario) {
        // Proprietário não tem comentários, mas precisa implementar o método abstrato
    }

}
