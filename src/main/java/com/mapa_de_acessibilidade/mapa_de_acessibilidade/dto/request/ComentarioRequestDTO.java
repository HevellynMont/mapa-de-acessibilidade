package com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ComentarioRequestDTO {

    @NotBlank(message = "Texto é obrigatório")
    private String texto;

    private Double nota;

    @NotNull(message = "ID do Usuário é obrigatório")
    private Long idUsuario;

    @NotNull(message = "ID do Local é obrigatório")
    private Long idLocal;

    private List<String> tags;

    public ComentarioRequestDTO() {
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Long idLocal) {
        this.idLocal = idLocal;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}