package com.mapa_de_acessibilidade.mapa_de_acessibilidade.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.ComentarioUsuarioDTO;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.ComentarioUsuario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.service.ComentarioService;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;

    @Autowired
    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    // DTO interno para receber dados do comentário
    public static class ComentarioRequest {
        private String descricao;
        private Integer nota;
        private Long pessoaId;
        private Set<Long> tagIds;

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
        
        public Integer getNota() { return nota; }
        public void setNota(Integer nota) { this.nota = nota; }
        
        public Long getPessoaId() { return pessoaId; }
        public void setPessoaId(Long pessoaId) { this.pessoaId = pessoaId; }
        
        public Set<Long> getTagIds() { return tagIds; }
        public void setTagIds(Set<Long> tagIds) { this.tagIds = tagIds; }
    }

    // pra criar um novo coment - recebe apenas IDs e faz associação no banco
    @PostMapping("/{localId}")
    public ResponseEntity<ComentarioUsuario> criarComentario(
            @PathVariable Long localId,
            @RequestBody ComentarioRequest request) {
        try {
            System.out.println("=== RECEBENDO COMENTÁRIO (JSON) ===");
            System.out.println("Local ID: " + localId);
            System.out.println("Pessoa ID: " + request.getPessoaId());
            System.out.println("Descrição: " + request.getDescricao());
            System.out.println("Nota: " + request.getNota());
            System.out.println("Tag IDs: " + request.getTagIds());
            
            Set<Long> tagIds = request.getTagIds();
            if (tagIds == null) {
                tagIds = new java.util.HashSet<>();
            }
            
            // Service faz toda a associação: busca Local, busca Pessoa, cria relacionamentos
            ComentarioUsuario novoComentario = comentarioService.salvarComentario(
                localId, 
                request.getPessoaId(), 
                request.getDescricao(),
                request.getNota(),
                tagIds
            );
            
            System.out.println("=== COMENTÁRIO SALVO COM SUCESSO ===");
            System.out.println("ID: " + novoComentario.getId());
            
            return new ResponseEntity<>(novoComentario, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.err.println("=== ERRO AO SALVAR COMENTÁRIO ===");
            System.err.println("Mensagem: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // DTO para resposta JSON
    public static class ComentarioResponse {
        private Long id;
        private String mensagem;
        
        public ComentarioResponse(Long id, String mensagem) {
            this.id = id;
            this.mensagem = mensagem;
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getMensagem() { return mensagem; }
        public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    }
    
    // Alternativa: INSERT direto via SQL nativo (sem JSON)
    @PostMapping("/direto/{localId}")
    public ResponseEntity<ComentarioResponse> criarComentarioDireto(
            @PathVariable Long localId,
            @RequestParam Long pessoaId,
            @RequestParam String descricao,
            @RequestParam Integer nota,
            @RequestParam(required = false) String tagIds) {
        try {
            System.out.println("=== INSERÇÃO DIRETA SQL ===");
            System.out.println("Local ID: " + localId);
            System.out.println("Pessoa ID: " + pessoaId);
            System.out.println("Descrição: " + descricao);
            System.out.println("Nota: " + nota);
            System.out.println("Tag IDs: " + tagIds);
            
            // Converte string de IDs para Set
            Set<Long> tagIdSet = new java.util.HashSet<>();
            if (tagIds != null && !tagIds.isEmpty()) {
                for (String id : tagIds.split(",")) {
                    tagIdSet.add(Long.parseLong(id.trim()));
                }
            }
            
            // Executa INSERT SQL direto
            Long comentarioId = comentarioService.salvarComentarioDireto(
                localId, 
                pessoaId, 
                descricao,
                nota,
                tagIdSet
            );
            
            System.out.println("=== INSERT DIRETO REALIZADO ===");
            System.out.println("ID do comentário: " + comentarioId);
            
            ComentarioResponse response = new ComentarioResponse(comentarioId, "Comentário criado com sucesso");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.err.println("=== ERRO NO INSERT DIRETO ===");
            System.err.println("Mensagem: " + e.getMessage());
            e.printStackTrace();
            ComentarioResponse errorResponse = new ComentarioResponse(null, "Erro: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    // buscar os comentarios por local
    @GetMapping("/local/{localId}")
    public ResponseEntity<List<ComentarioUsuario>> listarPorLocal(@PathVariable Long localId) {
        List<ComentarioUsuario> comentarios = comentarioService.buscarPorLocal(localId);
        if (comentarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(comentarios, HttpStatus.OK);
    }

    // buscar os comentarios por usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ComentarioUsuarioDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        System.out.println("=== BUSCANDO COMENTÁRIOS DO USUÁRIO: " + usuarioId + " ===");
        List<ComentarioUsuario> comentarios = comentarioService.buscarPorUsuario(usuarioId);
        System.out.println("Total de comentários encontrados: " + comentarios.size());
        
        if (comentarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        
        // Converter para DTO para evitar referências circulares
        List<ComentarioUsuarioDTO> comentariosDTO = comentarios.stream()
            .map(comentario -> {
                System.out.println("Processando comentário ID: " + comentario.getId());
                System.out.println("  - Descrição: " + comentario.getDescricao());
                System.out.println("  - Local: " + (comentario.getLocal() != null ? comentario.getLocal().getNome() : "NULL"));
                System.out.println("  - Tags: " + (comentario.getTagsComentadas() != null ? comentario.getTagsComentadas().size() : 0));
                return new ComentarioUsuarioDTO(comentario);
            })
            .collect(Collectors.toList());
        
        System.out.println("DTOs criados: " + comentariosDTO.size());
        return new ResponseEntity<>(comentariosDTO, HttpStatus.OK);
    }

    // deletar comentario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarComentario(@PathVariable Long id) {
        try {
            comentarioService.deletarComentario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // atualizar comentario
    @PutMapping("/{id}")
    public ResponseEntity<ComentarioUsuario> atualizarComentario(
            @PathVariable Long id,
            @RequestBody ComentarioUsuario comentarioAtualizado) {
        try {
            ComentarioUsuario comentario = comentarioService.atualizarComentario(id, comentarioAtualizado);
            return new ResponseEntity<>(comentario, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // atualizar tags de um comentario
    @PutMapping("/{id}/tags")
    public ResponseEntity<ComentarioUsuario> atualizarTagsComentario(
            @PathVariable Long id,
            @RequestBody Set<Long> tagIds) {
        try {
            ComentarioUsuario comentario = comentarioService.atualizarTagsComentario(id, tagIds);
            return new ResponseEntity<>(comentario, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}