package com.mapa_de_acessibilidade.mapa_de_acessibilidade.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.enums.TagAcessibilidadeEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/tags")
@Tag(name = "Tags de Acessibilidade")
public class TagAcessibilidadeController {

    @GetMapping
    @Operation(summary = "Listar tags disponíveis (Enum)")
    public ResponseEntity<List<TagAcessibilidadeEnum>> listar() {
        return ResponseEntity.ok(Arrays.asList(TagAcessibilidadeEnum.values()));
    }
}