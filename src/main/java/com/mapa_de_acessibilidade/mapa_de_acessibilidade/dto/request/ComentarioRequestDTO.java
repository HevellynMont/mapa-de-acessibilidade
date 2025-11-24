package com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request;

import java.util.List;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.enums.TagAcessibilidadeEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioRequestDTO {
    @NotBlank(message = "Texto é obrigatório")
    private String texto;

    private Double nota;

    @NotNull(message = "ID do Usuário é obrigatório")
    private Long idUsuario;

    @NotNull(message = "ID do Local é obrigatório")
    private Long idLocal;

    private List<TagAcessibilidadeEnum> tags;
}