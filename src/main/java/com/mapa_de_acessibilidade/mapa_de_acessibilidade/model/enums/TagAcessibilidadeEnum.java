package com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.enums;

public enum TagAcessibilidadeEnum {
    RAMPA("Rampa de Acesso", false),
    ELEVADOR("Elevador Adaptado", false),
    BANHEIRO("Banheiro Adaptado", false),
    PISO_TATIL("Piso Tátil", false),
    LIBRAS("Interprete de Libras", false),
    ESTACIONAMENTO("Estacionamento Prioritário", false),
    ENTRADA_LARGA("Entrada Larga", false),
    BRAILLE("Cardápio em Braille", false),
    INADEQUADO("Local Não Acessível / Inadequado", true);

    private final String descricao;
    private final boolean negativa;

    TagAcessibilidadeEnum(String descricao, boolean negativa) {
        this.descricao = descricao;
        this.negativa = negativa;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isNegativa() {
        return negativa;
    }
}