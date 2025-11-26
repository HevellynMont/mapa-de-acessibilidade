package com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String senha;

    public static UsuarioResponseDTO toResponseDTO(Usuario u) {
        return new UsuarioResponseDTO(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getSenha());
    }

    public static List<UsuarioResponseDTO> toResponsesDTO(List<Usuario> lista) {
        List<UsuarioResponseDTO> listaDTO = new ArrayList<>();
        for (Usuario u : lista) {
            listaDTO.add(toResponseDTO(u));
        }
        return listaDTO;
    }
}