package com.mapa_de_acessibilidade.mapa_de_acessibilidade.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Usuario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.UsuarioRepository;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository repo;

    public Usuario salvar(Usuario u) {
        if (repo.existsByEmail(u.getEmail()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado");
        return repo.save(u);
    }

    public List<Usuario> listar() {
        return repo.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    public Usuario atualizar(Long id, Usuario u) {
        Usuario banco = buscarPorId(id);
        BeanUtils.copyProperties(u, banco, "id", "comentarios");
        return repo.save(banco);
    }

    public void deletar(Long id) {
        if (!repo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Não encontrado");
        repo.deleteById(id);
    }
}