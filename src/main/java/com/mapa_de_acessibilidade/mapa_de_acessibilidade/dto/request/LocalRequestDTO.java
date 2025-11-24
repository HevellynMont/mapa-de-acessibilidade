package com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocalRequestDTO(
        @NotBlank String nome,
        @NotBlank String endereco,
        String descricao,
        @NotNull Long idProprietario) {
}