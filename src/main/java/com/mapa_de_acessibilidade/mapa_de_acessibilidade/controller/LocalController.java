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

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request.LocalRequestDTO;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.response.LocalResponseDTO;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Local;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.service.LocalService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/locais")
@Tag(name = "Locais")
public class LocalController {

    @Autowired
    private LocalService localService;

    @PostMapping
    public ResponseEntity<Local> criar(@RequestBody @Valid LocalRequestDTO dto) {
        Local local = new Local();
        BeanUtils.copyProperties(dto, local);
        Local salvo = localService.salvar(local, dto.getIdProprietario());
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<LocalResponseDTO>> listar() {
        return ResponseEntity.ok(LocalResponseDTO.toResponsesDTO(localService.listar()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocalResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid LocalRequestDTO dto) {
        Local atualizado = localService.atualizar(id, dto);
        return ResponseEntity.ok(LocalResponseDTO.toResponseDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        localService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}