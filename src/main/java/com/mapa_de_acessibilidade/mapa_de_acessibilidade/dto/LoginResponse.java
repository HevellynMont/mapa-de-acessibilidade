package com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto;

public class LoginResponse {
    
    private Long id;
    private String nome;
    private String email;
    private String tipo; // "usuario" ou "proprietario"
    private String mensagem;
    private boolean sucesso;
    
    // Construtores
    public LoginResponse() {
    }
    
    public LoginResponse(boolean sucesso, String mensagem) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
    }
    
    public LoginResponse(Long id, String nome, String email, String tipo, boolean sucesso, String mensagem) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipo = tipo;
        this.sucesso = sucesso;
        this.mensagem = mensagem;
    }
    
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
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getMensagem() {
        return mensagem;
    }
    
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
    
    public boolean isSucesso() {
        return sucesso;
    }
    
    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }
}
