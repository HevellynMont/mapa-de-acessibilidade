package com.mapa_de_acessibilidade.mapa_de_acessibilidade.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Usuario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.UsuarioRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UsuarioService {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    
    public List<Usuario> buscarTodos() {
        return usuarioRepository.findAll();
    }

 
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

   
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        logger.info("=== INICIANDO SALVAMENTO DE USUÁRIO ===");
        logger.info("Nome: {}", usuario.getNome());
        logger.info("Email: {}", usuario.getEmail());
        logger.info("Login: {}", usuario.getLogin());
        logger.info("Senha (hash): {}", usuario.getSenha());
        
        // Valida se email já existe
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            logger.error("Email já cadastrado: {}", usuario.getEmail());
            throw new RuntimeException("Email já cadastrado.");
        }
        
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        logger.info("Usuário salvo com ID: {}", usuarioSalvo.getId());
        logger.info("=== FIM DO SALVAMENTO DE USUÁRIO ===");
        
        return usuarioSalvo;
    }

    
    @Transactional
    public Usuario atualizarUsuario(Long id, Usuario detalhesUsuario) {
        return usuarioRepository.findById(id).map(usuarioExistente -> {
            // Valida email se foi alterado
            if (!usuarioExistente.getEmail().equals(detalhesUsuario.getEmail()) && 
                usuarioRepository.findByEmail(detalhesUsuario.getEmail()).isPresent()) {
                throw new RuntimeException("Email já cadastrado.");
            }
            
            usuarioExistente.setNome(detalhesUsuario.getNome());
            usuarioExistente.setEmail(detalhesUsuario.getEmail());
            usuarioExistente.setTelefone(detalhesUsuario.getTelefone());
            usuarioExistente.setLogin(detalhesUsuario.getLogin());
            
            if (detalhesUsuario.getSenha() != null) {
                usuarioExistente.setSenha(detalhesUsuario.getSenha());
            }
            
            return usuarioRepository.save(usuarioExistente);
        }).orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));
    }

    
    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    
    public List<Usuario> buscarUsuariosPorLocal(Long localId) {
        return usuarioRepository.findUsuariosByLocalId(localId);
    }

    
    public List<Usuario> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome);
    }
}
