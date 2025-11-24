package com.mapa_de_acessibilidade.mapa_de_acessibilidade.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Optional<Usuario> usuarioOpt = usuarioRepo.findById(idUser);
        if (usuarioOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }

        Optional<Local> localOpt = localRepo.findById(idLocal);
        if (localOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Local não encontrado");
        }

        c.setUsuario(usuarioOpt.get());
        c.setLocal(localOpt.get());

        if (tagsEnum != null) {
            c.setTags(new ArrayList<>(tagsEnum));
        } else {
            c.setTags(new ArrayList<>());
        }

        Comentario salvo = comentarioRepo.save(c);
        localService.recalcularReputacao(idLocal);
        return salvo;
    }

    public List<Comentario> listarPorLocal(Long idLocal) {
        return comentarioRepo.findByLocalId(idLocal);
    }

    public Comentario buscarPorId(Long id) {
        Optional<Comentario> compOpt = comentarioRepo.findById(id);
        if (compOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário não encontrado");
        }
        return compOpt.get();
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

        c.setTexto(dto.getTexto());

        if (dto.getTags() != null) {
            c.setTags(new ArrayList<>(dto.getTags()));
        }

        Comentario atualizado = comentarioRepo.save(c);
        localService.recalcularReputacao(c.getLocal().getId());
        return atualizado;
    }
}