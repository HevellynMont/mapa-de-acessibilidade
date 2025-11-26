package com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LocalRequestDTO {

        @NotBlank(message = "Nome é obrigatório")
        private String nome;

        @NotBlank(message = "Endereço é obrigatório")
        private String endereco;

        private String descricao;

        @NotNull(message = "ID do Proprietário é obrigatório")
        private Long idProprietario;

        public LocalRequestDTO() {
        }

        public String getNome() {
                return nome;
        }

        public void setNome(String nome) {
                this.nome = nome;
        }

        public String getEndereco() {
                return endereco;
        }

        public void setEndereco(String endereco) {
                this.endereco = endereco;
        }

        public String getDescricao() {
                return descricao;
        }

        public void setDescricao(String descricao) {
                this.descricao = descricao;
        }

        public Long getIdProprietario() {
                return idProprietario;
        }

        public void setIdProprietario(Long idProprietario) {
                this.idProprietario = idProprietario;
        }
}