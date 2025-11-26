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

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.controller.openapi.ProprietarioControllerOpenAPI;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request.ProprietarioRequestDTO;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.response.ProprietarioResponseDTO;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Proprietario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.service.ProprietarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/proprietarios")
public class ProprietarioController implements ProprietarioControllerOpenAPI {

    @Autowired
    private ProprietarioService service;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody @Valid ProprietarioRequestDTO dto) {
        Proprietario p = new Proprietario();

        BeanUtils.copyProperties(dto, p);

        Proprietario salvo = service.salvar(p);

        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();

        return ResponseEntity.created(uri).body(ProprietarioResponseDTO.toResponseDTO(salvo));
    }

    @GetMapping
    public ResponseEntity<List<ProprietarioResponseDTO>> listar() {
        return ResponseEntity.ok(ProprietarioResponseDTO.toResponsesDTO(service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProprietarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ProprietarioResponseDTO.toResponseDTO(service.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody @Valid ProprietarioRequestDTO dto) {
        Proprietario p = new Proprietario();
        BeanUtils.copyProperties(dto, p);
        return ResponseEntity.ok(ProprietarioResponseDTO.toResponseDTO(service.atualizar(id, p)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}