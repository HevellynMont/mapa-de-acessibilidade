package com.mapa_de_acessibilidade.mapa_de_acessibilidade.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


/**
 * Classe abstrata Pessoa que representa a superclasse para Usuario e Proprietario.
 * Utiliza estratégia de herança JOINED para criar tabelas separadas para cada subclasse.
 */
@Entity
@Table(name = "pessoa")
@Inheritance(strategy = InheritanceType.JOINED)

public abstract class Pessoa {

    // Construtor Padrão (necessário para JPA)
    public Pessoa() {
    }

    // Construtor com todos os campos (exceto id)
    public Pessoa(String nome, String email, String telefone, String login, Integer senha) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.login = login;
        this.senha = senha;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String telefone;

    @NotBlank(message = "O login é obrigatório")
    @Column(nullable = false, unique = true, length = 50)
    private String login;

    @NotNull(message = "A senha é obrigatória")
    @Column(nullable = false)
    private Integer senha;

    // Getters e Setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Integer getSenha() {
        return senha;
    }

    public void setSenha(Integer senha) {
        this.senha = senha;
    }

    public abstract void removerComentario(ComentarioUsuario comentario);
}

