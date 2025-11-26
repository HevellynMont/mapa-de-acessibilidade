package com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Local;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LocalResponseDTO {
    private Long id;
    private String nome;
    private String endereco;
    private String descricao;
    private Double latitude;
    private Double longitude;
    private Long idProprietario;
    private String nomeProprietario;
    private Double mediaAvaliacao;
    private Boolean possuiSelo;

    public static LocalResponseDTO toResponseDTO(Local l) {
        return new LocalResponseDTO(
                l.getId(),
                l.getNome(),
                l.getEndereco(),
                l.getDescricao(),
                l.getLatitude(),
                l.getLongitude(),
                l.getProprietario() != null ? l.getProprietario().getId() : null,
                l.getProprietario() != null ? l.getProprietario().getNome() : "Desconhecido",
                l.getMediaAvaliacao(),
                l.getPossuiSelo());
    }

    public static List<LocalResponseDTO> toResponsesDTO(List<Local> lista) {
        List<LocalResponseDTO> listaDTO = new ArrayList<>();

        for (Local l : lista) {
            LocalResponseDTO dto = toResponseDTO(l);
            listaDTO.add(dto);
        }

        return listaDTO;
    }
}