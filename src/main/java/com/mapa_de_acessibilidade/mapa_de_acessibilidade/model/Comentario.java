package com.mapa_de_acessibilidade.mapa_de_acessibilidade.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.enums.TagAcessibilidadeEnum;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comentario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(nullable = false, length = 1000)
    @ToString.Include
    private String texto;

    private Double nota;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_local", nullable = false)
    private Local local;

    @ElementCollection(targetClass = TagAcessibilidadeEnum.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "comentario_tags", joinColumns = @JoinColumn(name = "id_comentario"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    @OrderColumn(name = "tag_order")
    private List<TagAcessibilidadeEnum> tags = new ArrayList<>();
}