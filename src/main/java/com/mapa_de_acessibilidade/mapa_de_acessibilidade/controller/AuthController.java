package com.mapa_de_acessibilidade.mapa_de_acessibilidade.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.CadastroRequest;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.LoginRequest;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.LoginResponse;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Proprietario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Usuario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.ProprietarioRepository;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.UsuarioRepository;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.service.ProprietarioService;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ProprietarioRepository proprietarioRepository;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private ProprietarioService proprietarioService;

    /**
     * Endpoint para login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Converte a senha de String para Integer (hashCode)
            Integer senhaHash = loginRequest.getSenha().hashCode();
            
            String tipo = loginRequest.getTipo();
            
            if (tipo == null || tipo.isEmpty()) {
                tipo = "usuario"; // Padrão
            }
            
            if ("proprietario".equalsIgnoreCase(tipo)) {
                // Login como proprietário
                Optional<Proprietario> proprietarioOpt = proprietarioRepository
                    .findByEmailAndSenha(loginRequest.getEmail(), senhaHash);
                
                if (proprietarioOpt.isPresent()) {
                    Proprietario proprietario = proprietarioOpt.get();
                    LoginResponse response = new LoginResponse(
                        proprietario.getId(),
                        proprietario.getNome(),
                        proprietario.getEmail(),
                        "proprietario",
                        true,
                        "Login realizado com sucesso!"
                    );
                    return ResponseEntity.ok(response);
                }
            } else {
                // Login como usuário
                Optional<Usuario> usuarioOpt = usuarioRepository
                    .findByEmailAndSenha(loginRequest.getEmail(), senhaHash);
                
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    LoginResponse response = new LoginResponse(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        "usuario",
                        true,
                        "Login realizado com sucesso!"
                    );
                    return ResponseEntity.ok(response);
                }
            }
            
            // Credenciais inválidas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new LoginResponse(false, "Email ou senha incorretos"));
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new LoginResponse(false, "Erro ao processar login: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para cadastro
     */
    @PostMapping("/cadastro")
    public ResponseEntity<LoginResponse> cadastro(@Valid @RequestBody CadastroRequest cadastroRequest) {
        try {
            String tipo = cadastroRequest.getTipo();
            System.out.println("=== DEBUG CADASTRO ===");
            System.out.println("Tipo recebido: [" + tipo + "]");
            System.out.println("Nome: " + cadastroRequest.getNome());
            System.out.println("Email: " + cadastroRequest.getEmail());
            
            if (tipo == null || tipo.isEmpty()) {
                tipo = "usuario"; // Padrão
            }
            
            System.out.println("Tipo após validação: [" + tipo + "]");
            System.out.println("Comparação com 'proprietario': " + "proprietario".equalsIgnoreCase(tipo));

            // Converte a senha para hashCode
            Integer senhaHash = cadastroRequest.getSenha().hashCode();

            // Garante que o campo login seja igual ao email
            String login = cadastroRequest.getEmail();

            if ("proprietario".equalsIgnoreCase(tipo)) {
                System.out.println(">>> BRANCH: Cadastrando PROPRIETÁRIO");
                // Cadastro de proprietário
                Proprietario proprietario = new Proprietario(
                    cadastroRequest.getNome(),
                    cadastroRequest.getEmail(),
                    cadastroRequest.getTelefone(),
                    login, // login = email
                    senhaHash
                );

                Proprietario proprietarioSalvo = proprietarioService.salvarProprietario(proprietario);

                LoginResponse response = new LoginResponse(
                    proprietarioSalvo.getId(),
                    proprietarioSalvo.getNome(),
                    proprietarioSalvo.getEmail(),
                    "proprietario",
                    true,
                    "Cadastro realizado com sucesso!"
                );
                return ResponseEntity.status(HttpStatus.CREATED).body(response);

            } else {
                System.out.println(">>> BRANCH: Cadastrando USUÁRIO COMUM");
                // Cadastro de usuário
                Usuario usuario = new Usuario(
                    cadastroRequest.getNome(),
                    cadastroRequest.getEmail(),
                    cadastroRequest.getTelefone(),
                    login, // login = email
                    senhaHash
                );

                Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);

                LoginResponse response = new LoginResponse(
                    usuarioSalvo.getId(),
                    usuarioSalvo.getNome(),
                    usuarioSalvo.getEmail(),
                    "usuario",
                    true,
                    "Cadastro realizado com sucesso!"
                );
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new LoginResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new LoginResponse(false, "Erro ao processar cadastro: " + e.getMessage()));
        }
    }
}
