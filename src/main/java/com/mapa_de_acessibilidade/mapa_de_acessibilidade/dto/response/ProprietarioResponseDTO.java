package com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Proprietario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProprietarioResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String senha;

    public static ProprietarioResponseDTO toResponseDTO(Proprietario p) {
        return new ProprietarioResponseDTO(
                p.getId(),
                p.getNome(),
                p.getEmail(),
                p.getSenha());
    }

    public static List<ProprietarioResponseDTO> toResponsesDTO(List<Proprietario> lista) {
        List<ProprietarioResponseDTO> listaDTO = new ArrayList<>();
        for (Proprietario p : lista) {
            listaDTO.add(toResponseDTO(p));
        }
        return listaDTO;
    }
}