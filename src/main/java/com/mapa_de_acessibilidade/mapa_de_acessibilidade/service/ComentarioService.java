package com.mapa_de_acessibilidade.mapa_de_acessibilidade.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.ComentarioUsuario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Icomentario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Local;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.TagAcessibilidade;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.ComentarioRepository;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.LocalRepository;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.TagAcessibilidadeRepository;

@Service
public class ComentarioService implements Icomentario {

	private final ComentarioRepository comentarioRepository;
	private final LocalRepository localRepository;
	private final TagAcessibilidadeRepository tagRepository;
	private final LocalService localService;
	private final com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.PessoaRepository pessoaRepository;
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public ComentarioService(ComentarioRepository comentarioRepository, LocalRepository localRepository,
			TagAcessibilidadeRepository tagRepository, LocalService localService,
			com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.PessoaRepository pessoaRepository,
			JdbcTemplate jdbcTemplate) {
		super();
		this.comentarioRepository = comentarioRepository;
		this.localRepository = localRepository;
		this.tagRepository = tagRepository;
		this.localService = localService;
		this.pessoaRepository = pessoaRepository;
		this.jdbcTemplate = jdbcTemplate;
	}
	
	//crud ------------------------

	@Transactional    
	//create - inserção direta no banco associando apenas IDs
	public ComentarioUsuario salvarComentario(Long localId, Long pessoaId, String descricao, Integer nota, Set<Long> tagIds) {
		// Busca o Local
		Local local = localRepository.findById(localId)
			.orElseThrow(() -> new RuntimeException("Local não encontrado com ID: " + localId));

		// Busca a Pessoa
		com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Pessoa pessoa = pessoaRepository.findById(pessoaId)
			.orElseThrow(() -> new RuntimeException("Pessoa não encontrada com ID: " + pessoaId));
		
		// Verifica se é Usuario (não pode ser Proprietario)
		if (!(pessoa instanceof com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Usuario)) {
			throw new RuntimeException("Apenas usuários podem fazer comentários. Proprietários não podem comentar.");
		}

		// Cria o comentário com os dados recebidos
		ComentarioUsuario comentario = new ComentarioUsuario();
		comentario.setDescricao(descricao);
		comentario.setNota(nota);
		comentario.setPessoa(pessoa);
		comentario.setLocal(local);

		// Valida
		if (!validarComentario(comentario)) {
			throw new RuntimeException("Comentário inválido: verifique descrição e nota (1 a 5).");
		}

		// Busca e associa as tags
		Set<TagAcessibilidade> tags = atribuirTags(tagIds);
		comentario.setTagsComentadas(tags);

		// Salva no banco
		ComentarioUsuario salvo = comentarioRepository.save(comentario);
		
		// Atualiza score das tags no local
		for (TagAcessibilidade tag : tags) {
			Double novoScore = calcularNovoScore(local.getId(), tag.getId(), nota);
			localService.atualizarConfiancaTag(local.getId(), tag.getId(), novoScore);
		}

		return salvo;
	}

	// INSERT SQL direto sem JPA/Jackson - igual ao cadastro
	@Transactional
	public Long salvarComentarioDireto(Long localId, Long pessoaId, String descricao, Integer nota, Set<Long> tagIds) {
		// Valida entrada
		if (descricao == null || descricao.isBlank()) {
			throw new RuntimeException("Descrição é obrigatória");
		}
		if (nota == null || nota < 1 || nota > 5) {
			throw new RuntimeException("Nota deve estar entre 1 e 5");
		}
		if (localId == null || pessoaId == null) {
			throw new RuntimeException("localId e pessoaId são obrigatórios");
		}

		// Verifica se local existe
		if (!localRepository.existsById(localId)) {
			throw new RuntimeException("Local não encontrado com ID: " + localId);
		}

		// Verifica se pessoa existe e é usuario
		if (!pessoaRepository.existsById(pessoaId)) {
			throw new RuntimeException("Pessoa não encontrada com ID: " + pessoaId);
		}

		// INSERT direto na tabela comentario_usuario
		String sqlInsertComentario = 
			"INSERT INTO comentario_usuario (descricao, nota, pessoa_id, local_id, data_criacao) " +
			"VALUES (?, ?, ?, ?, NOW())";
		
		jdbcTemplate.update(sqlInsertComentario, descricao, nota, pessoaId, localId);

		// Busca o ID do comentário inserido
		Long comentarioId = jdbcTemplate.queryForObject(
			"SELECT LAST_INSERT_ID()", 
			Long.class
		);

		// INSERT das tags na tabela comentario_tag
		if (tagIds != null && !tagIds.isEmpty()) {
			String sqlInsertTag = "INSERT INTO comentario_tag (comentario_id, tag_id) VALUES (?, ?)";
			for (Long tagId : tagIds) {
				// Verifica se tag existe
				if (tagRepository.existsById(tagId)) {
					jdbcTemplate.update(sqlInsertTag, comentarioId, tagId);
					
					// Atualiza score da tag no local
					Double novoScore = calcularNovoScore(localId, tagId, nota);
					localService.atualizarConfiancaTag(localId, tagId, novoScore);
				}
			}
		}

		return comentarioId;
	}

	//read
	public List<ComentarioUsuario> buscarPorLocal(Long localId) {
		return comentarioRepository.findByLocalIdOrderByDataCriacaoDesc(localId);
	}

	//read por usuario
	public List<ComentarioUsuario> buscarPorUsuario(Long usuarioId) {
		// Usar query com JOIN FETCH para carregar Local e Tags
		return comentarioRepository.findByUsuarioComLocal(usuarioId);
	}

	//delete
	public void deletarComentario(Long id) {
		if (!comentarioRepository.existsById(id)) {
			throw new RuntimeException("comentário não encontrado.");
		}
		comentarioRepository.deleteById(id);
	}

	//update
	@Transactional
	public ComentarioUsuario atualizarComentario(Long id, ComentarioUsuario comentarioAtualizado) {
		ComentarioUsuario comentario = comentarioRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Comentário não encontrado."));
		
		if (comentarioAtualizado.getDescricao() != null && !comentarioAtualizado.getDescricao().isBlank()) {
			comentario.setDescricao(comentarioAtualizado.getDescricao());
		}
		
		if (comentarioAtualizado.getNota() != null) {
			comentario.setNota(comentarioAtualizado.getNota());
		}
		
		return comentarioRepository.save(comentario);
	}

	//update tags
	@Transactional
	public ComentarioUsuario atualizarTagsComentario(Long id, Set<Long> tagIds) {
		ComentarioUsuario comentario = comentarioRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Comentário não encontrado."));
		
		// Buscar e associar as novas tags
		Set<TagAcessibilidade> novasTags = atribuirTags(tagIds);
		comentario.setTagsComentadas(novasTags);
		
		// Salvar comentário com as novas tags
		ComentarioUsuario atualizado = comentarioRepository.save(comentario);
		
		// Recalcular scores das tags no local
		Local local = comentario.getLocal();
		Integer nota = comentario.getNota();
		
		for (TagAcessibilidade tag : novasTags) {
			Double novoScore = calcularNovoScore(local.getId(), tag.getId(), nota);
			localService.atualizarConfiancaTag(local.getId(), tag.getId(), novoScore);
		}
		
		return atualizado;
	}

	//metodos da interface
	@Override
	public boolean validarComentario(ComentarioUsuario comentario) {
		if (comentario.getNota() == null || comentario.getNota() < 1 || comentario.getNota() > 5)
			return false;
		if (comentario.getDescricao() == null || comentario.getDescricao().isBlank())
			return false;
		return true;
	}

	@Override
	public Set<TagAcessibilidade> atribuirTags(Set<Long> tagIds) {
		return new HashSet<>(tagRepository.findAllById(tagIds));
	}


	private Double calcularNovoScore(Long localId, Long tagId, Integer novaNota) {
		List<ComentarioUsuario> comentarios = comentarioRepository.findByLocalIdOrderByDataCriacaoDesc(localId);
		int soma = 0;
		int contador = 0;
		for (ComentarioUsuario c : comentarios) {
			for (TagAcessibilidade tag : c.getTagsComentadas()) {
				if (tag.getId().equals(tagId)) {
					soma += c.getNota();
					contador++;
				}
			}
		}
		soma += novaNota;
		contador++;

		return (double) soma / contador;
	}
}
