package com.mapa_de_acessibilidade.mapa_de_acessibilidade.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;

/**
 * Classe Usuario que representa um usuário do sistema,
 * herda de Pessoa e pode fazer comentários sobre locais.
 */
@Entity
@Table(name = "usuario")

@EqualsAndHashCode(callSuper = true)
public class Usuario extends Pessoa {

    // Construtor Padrão (necessário para JPA)
    public Usuario() {
        super();
    }

    // Construtor com todos os campos (chama o construtor de Pessoa)
    public Usuario(String nome, String email, String telefone, String login, Integer senha) {
        super(nome, email, telefone, login, senha);
    }

    /**
     * Relacionamento com Comentario: um usuário pode fazer vários comentários.
     */
    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ComentarioUsuario> comentarios = new HashSet<>();

    // Getters e Setters
    public Set<ComentarioUsuario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(Set<ComentarioUsuario> comentarios) {
        this.comentarios = comentarios;
    }

   
    public void adicionarComentario(ComentarioUsuario comentario) {
        comentarios.add(comentario);
        comentario.setPessoa(this);
    }

    
    @Override
    public void removerComentario(ComentarioUsuario comentario) {
        comentarios.remove(comentario);
        comentario.setPessoa(null);
    }

}
