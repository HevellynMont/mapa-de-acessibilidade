package com.mapa_de_acessibilidade.mapa_de_acessibilidade.controller;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.TagAcessibilidade;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.TagAcessibilidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagAcessibilidadeController {

    private final TagAcessibilidadeRepository tagRepository;

    @Autowired
    public TagAcessibilidadeController(TagAcessibilidadeRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    // Listar todas as tags
    @GetMapping
    public ResponseEntity<List<TagAcessibilidade>> listarTodas() {
        List<TagAcessibilidade> tags = tagRepository.findAll();
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    // Buscar tag por ID
    @GetMapping("/{id}")
    public ResponseEntity<TagAcessibilidade> buscarPorId(@PathVariable Long id) {
        return tagRepository.findById(id)
                .map(tag -> new ResponseEntity<>(tag, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Buscar tag por nome
    @GetMapping("/nome/{nome}")
    public ResponseEntity<TagAcessibilidade> buscarPorNome(@PathVariable String nome) {
        return tagRepository.findByNome(nome)
                .map(tag -> new ResponseEntity<>(tag, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Criar nova tag (apenas admin)
    @PostMapping
    public ResponseEntity<TagAcessibilidade> criar(@RequestBody TagAcessibilidade tag) {
        try {
            TagAcessibilidade novaTag = tagRepository.save(tag);
            return new ResponseEntity<>(novaTag, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Atualizar tag (apenas admin)
    @PutMapping("/{id}")
    public ResponseEntity<TagAcessibilidade> atualizar(
            @PathVariable Long id,
            @RequestBody TagAcessibilidade tagAtualizada) {
        return tagRepository.findById(id)
                .map(tag -> {
                    tag.setNome(tagAtualizada.getNome());
                    tag.setIconeUrl(tagAtualizada.getIconeUrl());
                    TagAcessibilidade tagSalva = tagRepository.save(tag);
                    return new ResponseEntity<>(tagSalva, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Deletar tag (apenas admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (tagRepository.existsById(id)) {
            tagRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
