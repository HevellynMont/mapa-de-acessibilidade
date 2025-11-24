package com.mapa_de_acessibilidade.mapa_de_acessibilidade.controller.openapi;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request.ComentarioRequestDTO;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.response.ComentarioResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Comentários", description = "Gerenciamento de avaliações e comentários")
public interface ComentarioControllerOpenAPI {

    @Operation(summary = "Adicionar comentário", description = "Cria um novo comentário para um local específico")
    @ApiResponse(responseCode = "201", description = "Comentário criado com sucesso")
    @ApiResponse(responseCode = "404", description = "Usuário ou Local não encontrado")
    ResponseEntity<ComentarioResponseDTO> criar(
            @Parameter(description = "Dados do comentário") ComentarioRequestDTO dto);

    @Operation(summary = "Listar por Local", description = "Lista todos os comentários de um determinado local")
    @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso")
    ResponseEntity<List<ComentarioResponseDTO>> listarPorLocal(@Parameter(description = "ID do local") Long idLocal);

    @Operation(summary = "Atualizar comentário", description = "Atualiza o texto ou tags de um comentário existente")
    @ApiResponse(responseCode = "200", description = "Comentário atualizado")
    @ApiResponse(responseCode = "404", description = "Comentário não encontrado")
    ResponseEntity<ComentarioResponseDTO> atualizar(
            @Parameter(description = "ID do comentário") Long id,
            @Parameter(description = "Dados atualizados") ComentarioRequestDTO dto);

    @Operation(summary = "Excluir comentário", description = "Remove um comentário e recalcula a nota do local")
    @ApiResponse(responseCode = "204", description = "Comentário excluído")
    @ApiResponse(responseCode = "404", description = "Comentário não encontrado")
    ResponseEntity<Void> deletar(@Parameter(description = "ID do comentário") Long id);
}