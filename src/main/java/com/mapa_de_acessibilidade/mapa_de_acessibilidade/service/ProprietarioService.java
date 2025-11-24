package com.mapa_de_acessibilidade.mapa_de_acessibilidade.service;

import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Proprietario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.ProprietarioRepository;

@Service
public class ProprietarioService {
    @Autowired
    private ProprietarioRepository repo;

    public Proprietario salvar(Proprietario p) {
        if (repo.existsByEmail(p.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado.");
        }
        return repo.save(p);
    }

    public List<Proprietario> listar() {
        return repo.findAll();
    }

    public Proprietario buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proprietário não encontrado"));
    }

    public Proprietario atualizar(Long id, Proprietario p) {
        Proprietario banco = buscarPorId(id);
        BeanUtils.copyProperties(p, banco, "id", "locais");
        return repo.save(banco);
    }

    public void deletar(Long id) {
        if (!repo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Não encontrado");
        repo.deleteById(id);
    }
}