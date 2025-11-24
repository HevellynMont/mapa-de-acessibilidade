package com.mapa_de_acessibilidade.mapa_de_acessibilidade.model;

import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    @JsonIgnore
    private Set<Comentario> comentarios = new HashSet<>();
}