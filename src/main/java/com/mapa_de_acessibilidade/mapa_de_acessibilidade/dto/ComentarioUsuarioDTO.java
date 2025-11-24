package com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.ComentarioUsuario;

public class ComentarioUsuarioDTO {
    
    private Long id;
    private String descricao;
    private Integer nota;
    private LocalDateTime dataCriacao;
    private LocalSimplificadoDTO local;
    private Set<TagSimplificadaDTO> tagsComentadas;
    
    // Construtor vazio
    public ComentarioUsuarioDTO() {}
    
    // Construtor a partir da entidade
    public ComentarioUsuarioDTO(ComentarioUsuario comentario) {
        this.id = comentario.getId();
        this.descricao = comentario.getDescricao();
        this.nota = comentario.getNota();
        this.dataCriacao = comentario.getDataCriacao();
        
        // Local simplificado - SEMPRE buscar, mesmo com @JsonBackReference
        if (comentario.getLocal() != null) {
            this.local = new LocalSimplificadoDTO(
                comentario.getLocal().getId(),
                comentario.getLocal().getNome(),
                comentario.getLocal().getEndereco()
            );
        } else {
            // Se for null, criar um objeto padrão
            this.local = new LocalSimplificadoDTO(null, "Local não especificado", "");
        }
        
        // Tags simplificadas
        if (comentario.getTagsComentadas() != null && !comentario.getTagsComentadas().isEmpty()) {
            this.tagsComentadas = comentario.getTagsComentadas().stream()
                .map(tag -> new TagSimplificadaDTO(tag.getId(), tag.getNome(), tag.getIconeUrl()))
                .collect(Collectors.toSet());
        } else {
            this.tagsComentadas = new java.util.HashSet<>();
        }
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public Integer getNota() {
        return nota;
    }
    
    public void setNota(Integer nota) {
        this.nota = nota;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    public LocalSimplificadoDTO getLocal() {
        return local;
    }
    
    public void setLocal(LocalSimplificadoDTO local) {
        this.local = local;
    }
    
    public Set<TagSimplificadaDTO> getTagsComentadas() {
        return tagsComentadas;
    }
    
    public void setTagsComentadas(Set<TagSimplificadaDTO> tagsComentadas) {
        this.tagsComentadas = tagsComentadas;
    }
    
    // Classes internas para dados simplificados
    public static class LocalSimplificadoDTO {
        private Long id;
        private String nome;
        private String endereco;
        
        public LocalSimplificadoDTO() {}
        
        public LocalSimplificadoDTO(Long id, String nome, String endereco) {
            this.id = id;
            this.nome = nome;
            this.endereco = endereco;
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getEndereco() { return endereco; }
        public void setEndereco(String endereco) { this.endereco = endereco; }
    }
    
    public static class TagSimplificadaDTO {
        private Long id;
        private String nome;
        private String iconeUrl;
        
        public TagSimplificadaDTO() {}
        
        public TagSimplificadaDTO(Long id, String nome, String iconeUrl) {
            this.id = id;
            this.nome = nome;
            this.iconeUrl = iconeUrl;
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getIconeUrl() { return iconeUrl; }
        public void setIconeUrl(String iconeUrl) { this.iconeUrl = iconeUrl; }
    }
}
