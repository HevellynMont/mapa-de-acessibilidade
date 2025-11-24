package com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request;

import java.util.List;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.enums.TagAcessibilidadeEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ComentarioRequestDTO(
                @NotBlank String texto,
                Double nota,
                @NotNull Long idUsuario,
                @NotNull Long idLocal,
                List<TagAcessibilidadeEnum> tags) {
}