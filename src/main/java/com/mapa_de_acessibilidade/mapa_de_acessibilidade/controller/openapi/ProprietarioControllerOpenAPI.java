package com.mapa_de_acessibilidade.mapa_de_acessibilidade.controller.openapi;

import java.util.List;
import org.springframework.http.ResponseEntity;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request.ProprietarioRequestDTO;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.response.ProprietarioResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Proprietário")
public interface ProprietarioControllerOpenAPI {
    @Operation(summary = "Criar proprietário")
    ResponseEntity<?> criar(@Valid ProprietarioRequestDTO dto);

    @Operation(summary = "Listar todos")
    ResponseEntity<List<ProprietarioResponseDTO>> listar();

    @Operation(summary = "Buscar por ID")
    ResponseEntity<ProprietarioResponseDTO> buscarPorId(Long id);

    @Operation(summary = "Atualizar")
    ResponseEntity<?> atualizar(Long id, @Valid ProprietarioRequestDTO dto);

    @Operation(summary = "Deletar")
    ResponseEntity<Void> deletar(Long id);
}