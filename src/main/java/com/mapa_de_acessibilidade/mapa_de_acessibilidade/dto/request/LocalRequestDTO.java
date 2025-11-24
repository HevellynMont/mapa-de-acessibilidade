package com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalRequestDTO {
        @NotBlank(message = "Nome é obrigatório")
        private String nome;

        @NotBlank(message = "Endereço é obrigatório")
        private String endereco;

        private String descricao;

        @NotNull(message = "ID do Proprietário é obrigatório")
        private Long idProprietario;
}