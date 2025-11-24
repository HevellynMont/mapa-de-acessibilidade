package com.mapa_de_acessibilidade.mapa_de_acessibilidade.controller;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.LocalDTO;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Local;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.service.LocalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Define a classe como um controlador REST
@RestController
// Mapeamento base para todos os endpoints deste controlador
@RequestMapping("/api/locais") 
@CrossOrigin(origins = "*")
public class LocalController {

    private final LocalService localService;

    // Injeção de Dependência do LocalService
    @Autowired
    public LocalController(LocalService localService) {
        this.localService = localService;
    }

    /* ----------------------------------
     * 1. C (CREATE) - Cadastrar Novo Local
     * Endpoint: POST /api/locais
     * ---------------------------------- */
    @PostMapping
    // O Front-end envia o Local e uma lista de IDs de Tags
    public ResponseEntity<Local> criarLocal(@RequestBody Local local, 
                                            @RequestParam Set<Long> tagIds,
                                            @RequestParam Long proprietarioId) {
        try {
            Local novoLocal = localService.salvarLocal(local, tagIds, proprietarioId);
            return new ResponseEntity<>(novoLocal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @GetMapping
    public ResponseEntity<List<Local>> listarTodos() {
        List<Local> locais = localService.buscarTodos();
        return new ResponseEntity<>(locais, HttpStatus.OK);
    }

   
    @GetMapping("/{id}")
    public ResponseEntity<Local> buscarPorId(@PathVariable Long id) {
        return localService.buscarPorId(id)
                .map(local -> new ResponseEntity<>(local, HttpStatus.OK))
                .orElse(ResponseEntity.notFound().build());
    }



    @PutMapping("/{id}")
    public ResponseEntity<Local> atualizarLocal(
        @PathVariable Long id,
        @RequestBody Local detalhesLocal,
        @RequestParam(required = false) Set<Long> tagIds) {

        try {
            Local localAtualizado = localService.atualizarLocal(id, detalhesLocal, tagIds);
            return new ResponseEntity<>(localAtualizado, HttpStatus.OK);

        } catch (RuntimeException e) {

            if (e.getMessage().contains("Local não encontrado")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
}

    
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarLocal(@PathVariable Long id) {
        if (localService.buscarPorId(id).isPresent()) {
            localService.deletarLocal(id);
        
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
  
    @GetMapping("/proprietario/{proprietarioId}")
    public ResponseEntity<List<LocalDTO>> listarPorProprietario(@PathVariable Long proprietarioId) {
        System.out.println("=== BUSCANDO LOCAIS DO PROPRIETÁRIO: " + proprietarioId + " ===");
        List<Local> locais = localService.buscarPorProprietario(proprietarioId);
        System.out.println("Total de locais encontrados: " + locais.size());
        
        if (locais.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        
        List<LocalDTO> locaisDTO = locais.stream()
            .map(local -> {
                System.out.println("Processando local ID: " + local.getId() + " - " + local.getNome());
                return new LocalDTO(local);
            })
            .collect(Collectors.toList());
        return new ResponseEntity<>(locaisDTO, HttpStatus.OK);
    }
}