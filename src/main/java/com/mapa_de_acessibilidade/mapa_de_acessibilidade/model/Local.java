package com.mapa_de_acessibilidade.mapa_de_acessibilidade.model;

import java.util.HashSet;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema; // <--- IMPORTAR

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "local")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Local {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(nullable = false)
    @ToString.Include
    private String nome;
    
    @Column(nullable = false)
    private String endereco;
    
    @Column(length = 1000)
    private String descricao;
    
    private Double latitude;
    private Double longitude;

    private Double mediaAvaliacao = 0.0;
    private Boolean possuiSelo = false;

    @ManyToOne
    @JoinColumn(name = "id_proprietario", nullable = false)
    private Proprietario proprietario;

    @OneToMany(mappedBy = "local", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(hidden = true) // <--- ESCONDE DO SWAGGER PARA NÃO TRAVAR
    private Set<Comentario> comentarios = new HashSet<>();
}