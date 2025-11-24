package com.mapa_de_acessibilidade.mapa_de_acessibilidade.model;

import java.util.Set;

public interface Icomentario {
	
	 boolean validarComentario(ComentarioUsuario comentario);
	 Set<TagAcessibilidade> atribuirTags(Set<Long> tagIds);

}
