package com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Comentario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.enums.TagAcessibilidadeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ComentarioResponseDTO {
    private Long id;
    private String texto;
    private Double nota;
    private String nomeUsuario;
    private String nomeLocal;
    private List<TagAcessibilidadeEnum> tags;

    public static ComentarioResponseDTO toResponseDTO(Comentario c) {
        return new ComentarioResponseDTO(
                c.getId(),
                c.getTexto(),
                c.getNota(),
                c.getUsuario().getNome(),
                c.getLocal().getNome(),
                c.getTags());
    }

    public static List<ComentarioResponseDTO> toResponsesDTO(List<Comentario> lista) {
        List<ComentarioResponseDTO> dtos = new ArrayList<>();
        for (Comentario c : lista) {
            dtos.add(toResponseDTO(c));
        }
        return dtos;
    }
}