package com.mapa_de_acessibilidade.mapa_de_acessibilidade.controller.openapi;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request.LocalRequestDTO;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.response.LocalResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Locais", description = "Gerenciamento de locais acessíveis")
public interface LocalControllerOpenAPI {

    @Operation(summary = "Cadastrar novo local", description = "Cadastra um novo local e vincula a um proprietário")
    @ApiResponse(responseCode = "201", description = "Local cadastrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    ResponseEntity<LocalResponseDTO> criar(@Parameter(description = "Dados do novo local") LocalRequestDTO dto);

    @Operation(summary = "Listar locais", description = "Lista todos os locais cadastrados no sistema")
    @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso")
    ResponseEntity<List<LocalResponseDTO>> listar();

    @Operation(summary = "Atualizar local", description = "Atualiza dados de um local existente")
    @ApiResponse(responseCode = "200", description = "Local atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Local não encontrado")
    ResponseEntity<LocalResponseDTO> atualizar(
            @Parameter(description = "ID do local") Long id,
            @Parameter(description = "Dados atualizados") LocalRequestDTO dto);

    @Operation(summary = "Excluir local", description = "Remove um local do sistema")
    @ApiResponse(responseCode = "204", description = "Local excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Local não encontrado")
    ResponseEntity<Void> deletar(@Parameter(description = "ID do local") Long id);
}