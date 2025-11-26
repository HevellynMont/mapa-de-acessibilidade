package com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Interface;

import java.util.List;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Local;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Usuario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.enums.TagAcessibilidadeEnum;

public interface ComentarioInterface {
    String getTexto();

    void setTexto(String texto);

    Double getNota();

    void setNota(Double nota);

    Usuario getUsuario();

    void setUsuario(Usuario usuario);

    Local getLocal();

    void setLocal(Local local);

    List<TagAcessibilidadeEnum> getTags();

    void setTags(List<TagAcessibilidadeEnum> tags);
}