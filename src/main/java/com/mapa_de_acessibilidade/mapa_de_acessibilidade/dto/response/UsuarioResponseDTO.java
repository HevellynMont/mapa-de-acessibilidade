package com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.response;

import java.util.List;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;

    public static UsuarioResponseDTO toResponseDTO(Usuario u) {
        return new UsuarioResponseDTO(u.getId(), u.getNome(), u.getEmail());
    }

    public static List<UsuarioResponseDTO> toResponsesDTO(List<Usuario> lista) {
        return lista.stream().map(UsuarioResponseDTO::toResponseDTO).toList();
    }
}