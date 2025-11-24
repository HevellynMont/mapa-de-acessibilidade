package com.mapa_de_acessibilidade.mapa_de_acessibilidade.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comentario_usuario")
public class ComentarioUsuario implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;
    
    @Column(name = "nota", nullable = false)
    private Integer nota;
  
    @ManyToOne
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa pessoa;
    
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "local_id", nullable = false)
    @JsonBackReference
    private Local local;
   
    @ManyToMany
    @JoinTable(
        name = "comentario_tag",
        joinColumns = @JoinColumn(name = "comentario_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @JsonProperty("tags")
    private Set<TagAcessibilidade> tagsComentadas = new HashSet<>();
    
  
    public ComentarioUsuario(){

    }

	public ComentarioUsuario(Long id, String descricao, Integer nota, Pessoa pessoa, LocalDateTime dataCriacao,
			Local local, Set<TagAcessibilidade> tagsComentadas) {
		super();
		this.id = id;
		this.descricao = descricao;
		this.nota = nota;
		this.pessoa = pessoa;
		this.dataCriacao = dataCriacao;
		this.local = local;
		this.tagsComentadas = tagsComentadas;
	}


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


	public Pessoa getPessoa() {
		return pessoa;
	}


	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}


	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}


	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}


	public Local getLocal() {
		return local;
	}


	public void setLocal(Local local) {
		this.local = local;
	}


	public Set<TagAcessibilidade> getTagsComentadas() {
		return tagsComentadas;
	}


	public void setTagsComentadas(Set<TagAcessibilidade> tagsComentadas) {
		this.tagsComentadas = tagsComentadas;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}



}
