package com.mapa_de_acessibilidade.mapa_de_acessibilidade.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Proprietario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.ProprietarioRepository;


@Service
public class ProprietarioService {

    private final ProprietarioRepository proprietarioRepository;

    @Autowired
    public ProprietarioService(ProprietarioRepository proprietarioRepository) {
        this.proprietarioRepository = proprietarioRepository;
    }

    
    public List<Proprietario> buscarTodos() {
        return proprietarioRepository.findAll();
    }

    
    public Optional<Proprietario> buscarPorId(Long id) {
        return proprietarioRepository.findById(id);
    }

    
    @Transactional
    public Proprietario salvarProprietario(Proprietario proprietario) {
        // Valida se email já existe
        if (proprietarioRepository.findByEmail(proprietario.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado.");
        }
        return proprietarioRepository.save(proprietario);
    }

    
    @Transactional
    public Proprietario atualizarProprietario(Long id, Proprietario detalhesProprietario) {
        return proprietarioRepository.findById(id).map(proprietarioExistente -> {
            // Valida email se foi alterado
            if (!proprietarioExistente.getEmail().equals(detalhesProprietario.getEmail()) && 
                proprietarioRepository.findByEmail(detalhesProprietario.getEmail()).isPresent()) {
                throw new RuntimeException("Email já cadastrado.");
            }
            
            proprietarioExistente.setNome(detalhesProprietario.getNome());
            proprietarioExistente.setEmail(detalhesProprietario.getEmail());
            proprietarioExistente.setTelefone(detalhesProprietario.getTelefone());
            proprietarioExistente.setLogin(detalhesProprietario.getLogin());
            
            if (detalhesProprietario.getSenha() != null) {
                proprietarioExistente.setSenha(detalhesProprietario.getSenha());
            }
            
            return proprietarioRepository.save(proprietarioExistente);
        }).orElseThrow(() -> new RuntimeException("Proprietário não encontrado com ID: " + id));
    }

    
    public void deletarProprietario(Long id) {
        // Verifica se o proprietário possui locais antes de deletar
        Long countLocais = proprietarioRepository.countLocaisByProprietarioId(id);
        if (countLocais > 0) {
            throw new RuntimeException("Proprietário não pode ser deletado pois possui " + countLocais + " locais cadastrados.");
        }
        
        if (!proprietarioRepository.existsById(id)) {
            throw new RuntimeException("Proprietário não encontrado com ID: " + id);
        }
        proprietarioRepository.deleteById(id);
    }

    
    public List<Proprietario> buscarProprietariosPorCidade(String cidade) {
        return proprietarioRepository.findProprietariosByCidade(cidade);
    }

    public List<Proprietario> buscarPorNome(String nome) {
        return proprietarioRepository.findByNomeContainingIgnoreCase(nome);
    }

    
    public Long contarLocais(Long proprietarioId) {
        return proprietarioRepository.countLocaisByProprietarioId(proprietarioId);
    }
}
