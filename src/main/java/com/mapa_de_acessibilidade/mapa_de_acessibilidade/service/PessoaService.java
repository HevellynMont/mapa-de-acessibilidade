package com.mapa_de_acessibilidade.mapa_de_acessibilidade.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Pessoa;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.PessoaRepository;

@Service
public class PessoaService {

    private final PessoaRepository pessoaRepository;

    @Autowired
    public PessoaService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

   
    public List<Pessoa> buscarTodas() {
        return pessoaRepository.findAll();
    }

    
    public Optional<Pessoa> buscarPorId(Long id) {
        return pessoaRepository.findById(id);
    }

    
    public Optional<Pessoa> buscarPorEmail(String email) {
        return pessoaRepository.findByEmail(email);
    }

    
    public Optional<Pessoa> buscarPorLogin(String login) {
        return pessoaRepository.findByLogin(login);
    }

    
    @Transactional
    public Pessoa salvarPessoa(Pessoa pessoa) {
        if (pessoaRepository.existsByEmail(pessoa.getEmail())) {
            throw new RuntimeException("Email já cadastrado.");
        }
        if (pessoaRepository.existsByLogin(pessoa.getLogin())) {
            throw new RuntimeException("Login já cadastrado.");
        }
        
        return pessoaRepository.save(pessoa);
    }

   
    @Transactional
    public Pessoa atualizarPessoa(Long id, Pessoa detalhesPessoa) {
        return pessoaRepository.findById(id).map(pessoaExistente -> {
        
            if (!pessoaExistente.getEmail().equals(detalhesPessoa.getEmail()) && pessoaRepository.existsByEmail(detalhesPessoa.getEmail())) {
                throw new RuntimeException("Email já cadastrado.");
            }
        
            if (!pessoaExistente.getLogin().equals(detalhesPessoa.getLogin()) && pessoaRepository.existsByLogin(detalhesPessoa.getLogin())) {
                throw new RuntimeException("Login já cadastrado.");
            }

            pessoaExistente.setNome(detalhesPessoa.getNome());
            pessoaExistente.setEmail(detalhesPessoa.getEmail());
            pessoaExistente.setTelefone(detalhesPessoa.getTelefone());
            pessoaExistente.setLogin(detalhesPessoa.getLogin());
         
            if (detalhesPessoa.getSenha() != null) {
                pessoaExistente.setSenha(detalhesPessoa.getSenha());
            }

            return pessoaRepository.save(pessoaExistente);
        }).orElseThrow(() -> new RuntimeException("Pessoa não encontrada com ID: " + id));
    }


    public void deletarPessoa(Long id) {
        if (!pessoaRepository.existsById(id)) {
            throw new RuntimeException("Pessoa não encontrada com ID: " + id);
        }
        pessoaRepository.deleteById(id);
    }
}
