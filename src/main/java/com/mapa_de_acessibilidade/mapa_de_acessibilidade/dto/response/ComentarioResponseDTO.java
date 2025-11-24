package com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Comentario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.enums.TagAcessibilidadeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComentarioResponseDTO {
    
    private Long id;
    private String texto;
    private Long idUsuario;
    private String nomeUsuario; 
    private Long idLocal;
    private String nomeLocal;   
    private List<String> tags;

    public static ComentarioResponseDTO toResponseDTO(Comentario c) {
        ComentarioResponseDTO dto = new ComentarioResponseDTO();
        dto.setId(c.getId());
        dto.setTexto(c.getTexto());
        
        if (c.getUsuario() != null) {
            dto.setIdUsuario(c.getUsuario().getId());
            dto.setNomeUsuario(c.getUsuario().getNome());
            dto.setIdUsuario(0L);
            dto.setNomeUsuario("Anônimo");
        }

        if (c.getLocal() != null) {
            dto.setIdLocal(c.getLocal().getId());
            dto.setNomeLocal(c.getLocal().getNome());
        }
        
        List<String> tagsString = new ArrayList<>();
        if (c.getTags() != null) {
            for (TagAcessibilidadeEnum t : c.getTags()) {
                tagsString.add(t.name()); 
            }
        }
        dto.setTags(tagsString);

        return dto;
    }

    public static List<ComentarioResponseDTO> toResponsesDTO(List<Comentario> lista) {
        List<ComentarioResponseDTO> dtos = new ArrayList<>();
        for (Comentario c : lista) {
            dtos.add(toResponseDTO(c));
        }
        return dtos;
    }
}