package com.mapa_de_acessibilidade.mapa_de_acessibilidade.model;

import java.util.HashSet;
import java.util.Set;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Interface.ComentarioInterface;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.enums.TagAcessibilidadeEnum;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "comentario")
@NoArgsConstructor
public class Comentario implements ComentarioInterface {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String texto;

    private Double nota;

    @ManyToOne @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne @JoinColumn(name = "id_local", nullable = false)
    private Local local;

    @ElementCollection(targetClass = TagAcessibilidadeEnum.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "comentario_tags", joinColumns = @JoinColumn(name = "id_comentario"))
    @Enumerated(EnumType.STRING) // Salva "RAMPA", "ELEVADOR" no banco
    @Column(name = "tag")
    private Set<TagAcessibilidadeEnum> tags = new HashSet<>();
}