package com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Proprietario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProprietarioResponseDTO {
    private Long id;
    private String nome;
    private String email;

    public static ProprietarioResponseDTO toResponseDTO(Proprietario p) {
        return new ProprietarioResponseDTO(p.getId(), p.getNome(), p.getEmail());
    }

    public static List<ProprietarioResponseDTO> toResponsesDTO(List<Proprietario> lista) {
        List<ProprietarioResponseDTO> dtos = new ArrayList<>();
        for (Proprietario p : lista) {
            dtos.add(toResponseDTO(p));
        }
        return dtos;
    }
}