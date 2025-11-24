package com.mapa_de_acessibilidade.mapa_de_acessibilidade.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request.ComentarioRequestDTO;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Comentario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Local;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Usuario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.enums.TagAcessibilidadeEnum;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.ComentarioRepository;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.LocalRepository;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.UsuarioRepository;

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
        Usuario usuario = usuarioRepo.findById(idUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Local local = localRepo.findById(idLocal)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Local não encontrado"));

        c.setUsuario(usuario);
        c.setLocal(local);

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
        return comentarioRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário não encontrado"));
    }

    @Transactional
    public Comentario atualizar(Long id, ComentarioRequestDTO dto) {
        Comentario c = buscarPorId(id);

        c.setTexto(dto.getTexto());

        if (dto.getTags() != null) {
            List<TagAcessibilidadeEnum> tagsConvertidas = new ArrayList<>();
            for (String tagStr : dto.getTags()) {
                try {
                    tagsConvertidas.add(TagAcessibilidadeEnum.valueOf(tagStr));
                } catch (IllegalArgumentException e) {
                }
            }
            c.setTags(tagsConvertidas);
        }

        Comentario atualizado = comentarioRepo.save(c);
        localService.recalcularReputacao(c.getLocal().getId());
        return atualizado;
    }

    @Transactional
    public void deletar(Long id) {
        Comentario c = buscarPorId(id);
        Local local = c.getLocal();

        if (local != null && local.getComentarios() != null) {
            local.getComentarios().remove(c);
        }

        comentarioRepo.delete(c);
        comentarioRepo.flush();

        if (local != null) {
            localService.recalcularReputacao(local.getId());
        }
    }
}