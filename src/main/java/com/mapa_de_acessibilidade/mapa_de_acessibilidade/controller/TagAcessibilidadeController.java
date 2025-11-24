package com.mapa_de_acessibilidade.mapa_de_acessibilidade.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.enums.TagAcessibilidadeEnum;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/tags")
@Tag(name = "Tags de Acessibilidade")
public class TagAcessibilidadeController {

    @GetMapping
    public ResponseEntity<List<TagAcessibilidadeEnum>> listar() {
        List<TagAcessibilidadeEnum> listaTags = new ArrayList<>();

        for (TagAcessibilidadeEnum tag : TagAcessibilidadeEnum.values()) {
            listaTags.add(tag);
        }

        return ResponseEntity.ok(listaTags);
    }
}