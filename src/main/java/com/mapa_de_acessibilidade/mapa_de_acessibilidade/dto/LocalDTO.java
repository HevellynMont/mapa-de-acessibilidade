package com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto;

import java.util.Set;
import java.util.stream.Collectors;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Local;

public class LocalDTO {
    
    private Long id;
    private String nome;
    private String descricao;
    private String endereco;
    private double latitude;
    private double longitude;
    private Set<LocalTagDTO> tags;
    
    // Construtor vazio
    public LocalDTO() {}
    
    // Construtor a partir da entidade
    public LocalDTO(Local local) {
        this.id = local.getId();
        this.nome = local.getNome();
        this.descricao = local.getDescricao();
        this.endereco = local.getEndereco();
        this.latitude = local.getLatitude();
        this.longitude = local.getLongitude();
        
        // Tags com score
        if (local.getTagsComScore() != null && !local.getTagsComScore().isEmpty()) {
            this.tags = local.getTagsComScore().stream()
                .map(localTag -> new LocalTagDTO(
                    localTag.getTag().getId(),
                    localTag.getTag().getNome(),
                    localTag.getTag().getIconeUrl(),
                    localTag.getConfiancaScore()
                ))
                .collect(Collectors.toSet());
        } else {
            this.tags = new java.util.HashSet<>();
        }
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
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getEndereco() {
        return endereco;
    }
    
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    public Set<LocalTagDTO> getTags() {
        return tags;
    }
    
    public void setTags(Set<LocalTagDTO> tags) {
        this.tags = tags;
    }
    
    // Classe interna para tags
    public static class LocalTagDTO {
        private Long tagId;
        private String tagNome;
        private String tagIconeUrl;
        private Double confiancaScore;
        
        public LocalTagDTO() {}
        
        public LocalTagDTO(Long tagId, String tagNome, String tagIconeUrl, Double confiancaScore) {
            this.tagId = tagId;
            this.tagNome = tagNome;
            this.tagIconeUrl = tagIconeUrl;
            this.confiancaScore = confiancaScore;
        }
        
        public Long getTagId() { return tagId; }
        public void setTagId(Long tagId) { this.tagId = tagId; }
        public String getTagNome() { return tagNome; }
        public void setTagNome(String tagNome) { this.tagNome = tagNome; }
        public String getTagIconeUrl() { return tagIconeUrl; }
        public void setTagIconeUrl(String tagIconeUrl) { this.tagIconeUrl = tagIconeUrl; }
        public Double getConfiancaScore() { return confiancaScore; }
        public void setConfiancaScore(Double confiancaScore) { this.confiancaScore = confiancaScore; }
    }
}
