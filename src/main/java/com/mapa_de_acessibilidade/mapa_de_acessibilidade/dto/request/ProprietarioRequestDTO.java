package com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request;

import java.io.Serializable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ProprietarioRequestDTO(
        @NotBlank(message = "Nome é obrigatório") String nome,
        @NotBlank(message = "Email é obrigatório") @Email String email,
        @NotBlank(message = "Senha é obrigatória") String senha) implements Serializable {
}