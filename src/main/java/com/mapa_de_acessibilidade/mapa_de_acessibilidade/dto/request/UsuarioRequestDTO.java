package com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request;

import java.io.Serializable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioRequestDTO(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank String senha) implements Serializable {
}