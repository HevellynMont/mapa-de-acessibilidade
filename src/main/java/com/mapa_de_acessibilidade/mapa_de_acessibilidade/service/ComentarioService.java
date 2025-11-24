package com.mapa_de_acessibilidade.mapa_de_acessibilidade.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request.ComentarioRequestDTO;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Comentario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Local;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Usuario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.enums.TagAcessibilidadeEnum;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.ComentarioRepository;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.LocalRepository;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class ComentarioService {
    @Autowired
    private ComentarioRepository comentarioRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private LocalRepository localRepo;
    @Autowired
    private LocalService localService;

    @Transactional
    public Comentario salvar(Comentario c, Long idUser, Long idLocal, List<TagAcessibilidadeEnum> tagsEnum) {
        Usuario u = usuarioRepo.findById(idUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        Local l = localRepo.findById(idLocal)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Local não encontrado"));

        c.setUsuario(u);
        c.setLocal(l);
        c.setTags(new HashSet<>(tagsEnum));
        Comentario salvo = comentarioRepo.save(c);
        localService.recalcularReputacao(idLocal);
        return salvo;
    }

    public List<Comentario> listarPorLocal(Long idLocal) {
        return comentarioRepo.findByLocalId(idLocal);
    }

    public Comentario buscarPorId(Long id) {
        return comentarioRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário não encontrado"));
    }

    @Transactional
    public void deletar(Long id) {
        Comentario c = buscarPorId(id);
        Long idLocal = c.getLocal().getId();
        comentarioRepo.deleteById(id);
        localService.recalcularReputacao(idLocal);
    }

    @Transactional
    public Comentario atualizar(Long id, ComentarioRequestDTO dto) {
        Comentario c = buscarPorId(id);
        c.setTexto(dto.texto());

        if (dto.tags() != null) {
            c.setTags(new HashSet<>(dto.tags()));
        }

        Comentario atualizado = comentarioRepo.save(c);
        localService.recalcularReputacao(c.getLocal().getId());
        return atualizado;
    }
}