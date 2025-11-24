package com.mapa_de_acessibilidade.mapa_de_acessibilidade.controller;

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

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request.ComentarioRequestDTO;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Comentario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.service.ComentarioService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/comentarios")
@Tag(name = "Comentários")
public class ComentarioController {

    @Autowired
    private ComentarioService service;

    @PostMapping
    public ResponseEntity<Comentario> criar(@RequestBody @Valid ComentarioRequestDTO dto) {
        Comentario c = new Comentario();
        BeanUtils.copyProperties(dto, c);

        Comentario salvo = service.salvar(c, dto.getIdUsuario(), dto.getIdLocal(), dto.getTags());

        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(salvo);
    }

    @GetMapping("/local/{idLocal}")
    public ResponseEntity<List<Comentario>> listarPorLocal(@PathVariable Long idLocal) {
        return ResponseEntity.ok(service.listarPorLocal(idLocal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comentario> atualizar(@PathVariable Long id, @RequestBody @Valid ComentarioRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}