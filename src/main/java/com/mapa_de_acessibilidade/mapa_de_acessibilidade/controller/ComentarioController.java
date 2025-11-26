package com.mapa_de_acessibilidade.mapa_de_acessibilidade.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.controller.openapi.ComentarioControllerOpenAPI;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request.ComentarioRequestDTO;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.response.ComentarioResponseDTO;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Comentario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.enums.TagAcessibilidadeEnum;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.service.ComentarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/comentarios")
public class ComentarioController implements ComentarioControllerOpenAPI {

    @Autowired
    private ComentarioService service;

    @PostMapping
    public ResponseEntity<ComentarioResponseDTO> criar(@RequestBody @Valid ComentarioRequestDTO dto) {
        Comentario c = new Comentario();
        
        BeanUtils.copyProperties(dto, c);

        List<TagAcessibilidadeEnum> tagsEnum = new ArrayList<>();
        if (dto.getTags() != null) {
            for (String t : dto.getTags()) {
                try {
                    tagsEnum.add(TagAcessibilidadeEnum.valueOf(t));
                } catch (IllegalArgumentException e) {
                }
            }
        }
        Comentario salvo = service.salvar(c, dto.getIdUsuario(), dto.getIdLocal(), tagsEnum);

        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(salvo.getId()).toUri();

        return ResponseEntity.created(uri).body(ComentarioResponseDTO.toResponseDTO(salvo));
    }

    @GetMapping("/local/{idLocal}")
    public ResponseEntity<List<ComentarioResponseDTO>> listarPorLocal(@PathVariable Long idLocal) {
        List<Comentario> lista = service.listarPorLocal(idLocal);
        return ResponseEntity.ok(ComentarioResponseDTO.toResponsesDTO(lista));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComentarioResponseDTO> atualizar(@PathVariable Long id,
            @RequestBody @Valid ComentarioRequestDTO dto) {
        Comentario atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(ComentarioResponseDTO.toResponseDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}