package com.mapa_de_acessibilidade.mapa_de_acessibilidade.controller.openapi;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.enums.TagAcessibilidadeEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Tags", description = "Listagem de tags de acessibilidade disponíveis")
public interface TagAcessibilidadeControllerOpenAPI {

    @Operation(summary = "Listar todas as tags", description = "Retorna a lista de enums disponíveis para classificação de locais")
    @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso")
    ResponseEntity<List<TagAcessibilidadeEnum>> listar();
}