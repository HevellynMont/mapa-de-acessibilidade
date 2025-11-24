package com.mapa_de_acessibilidade.mapa_de_acessibilidade.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.controller.openapi.TagAcessibilidadeControllerOpenAPI;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.enums.TagAcessibilidadeEnum;

@RestController
@RequestMapping("/tags")
public class TagAcessibilidadeController implements TagAcessibilidadeControllerOpenAPI {

    @GetMapping
    public ResponseEntity<List<TagAcessibilidadeEnum>> listar() {
        List<TagAcessibilidadeEnum> listaTags = new ArrayList<>();

        for (TagAcessibilidadeEnum tag : TagAcessibilidadeEnum.values()) {
            listaTags.add(tag);
        }

        return ResponseEntity.ok(listaTags);
    }
}