package com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UsuarioRequestDTO implements Serializable {

        @NotBlank(message = "Nome é obrigatório")
        private String nome;

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ser válido")
        private String email;

        @NotBlank(message = "Senha é obrigatória")
        private String senha;

        public UsuarioRequestDTO() {
        }

        public UsuarioRequestDTO(String nome, String email, String senha) {
                this.nome = nome;
                this.email = email;
                this.senha = senha;
        }

        public String getNome() {
                return nome;
        }

        public void setNome(String nome) {
                this.nome = nome;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        public String getSenha() {
                return senha;
        }

        public void setSenha(String senha) {
                this.senha = senha;
        }
}