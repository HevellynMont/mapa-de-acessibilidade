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

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.controller.openapi.UsuarioControllerOpenAPI;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request.UsuarioRequestDTO;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.response.UsuarioResponseDTO;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Usuario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController implements UsuarioControllerOpenAPI {
    @Autowired
    private UsuarioService service;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody @Valid UsuarioRequestDTO dto) {
        Usuario u = new Usuario();
        BeanUtils.copyProperties(dto, u);
        Usuario salvo = service.salvar(u);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(UsuarioResponseDTO.toResponseDTO(salvo));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(UsuarioResponseDTO.toResponsesDTO(service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(UsuarioResponseDTO.toResponseDTO(service.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioRequestDTO dto) {
        Usuario u = new Usuario();
        BeanUtils.copyProperties(dto, u);
        return ResponseEntity.ok(UsuarioResponseDTO.toResponseDTO(service.atualizar(id, u)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}