package com.mapa_de_acessibilidade.mapa_de_acessibilidade.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.client.NominatimClient;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.client.dto.NominatimResponse;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Local;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.LocalTag;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Pessoa;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Proprietario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.TagAcessibilidade;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.LocalRepository;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.PessoaRepository;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.ProprietarioRepository;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.TagAcessibilidadeRepository;

@Service
public class LocalService {

		private final LocalRepository localRepository;
		private final TagAcessibilidadeRepository tagRepository;
		private final NominatimClient nominatimClient;
		private final ProprietarioRepository proprietarioRepository;
		private final PessoaRepository pessoaRepository;

		@Autowired
		public LocalService(LocalRepository localRepository, TagAcessibilidadeRepository tagRepository,
				NominatimClient nominatimClient, ProprietarioRepository proprietarioRepository,
				PessoaRepository pessoaRepository) {
			this.localRepository = localRepository;
			this.tagRepository = tagRepository;
			this.nominatimClient = nominatimClient;
			this.proprietarioRepository = proprietarioRepository;
			this.pessoaRepository = pessoaRepository;
		}

	/**
	 * @param local
	 * @param idsTags
	 * @return
	 */
	// @Transactional garante que a operação de banco será atômica
	@Transactional
		public Local salvarLocal(Local local, Set<Long> idsTags, Long proprietarioId) {
			// Primeiro tenta buscar como Proprietario
			Optional<Proprietario> proprietarioOpt = proprietarioRepository.findById(proprietarioId);
			
			if (proprietarioOpt.isPresent()) {
				local.setProprietario(proprietarioOpt.get());
			} else {
				// Se não encontrar como Proprietario, busca como Pessoa e cria um Proprietario
				Pessoa pessoa = pessoaRepository.findById(proprietarioId)
						.orElseThrow(() -> new RuntimeException("Usuário/Proprietário não encontrado com ID: " + proprietarioId));
				
				// Se for um Usuario tentando cadastrar, converte para Proprietario
				// Ou você pode permitir que Usuario também tenha locais - nesse caso, precisaria adaptar o modelo
				throw new RuntimeException("Apenas proprietários podem cadastrar locais. Por favor, cadastre-se como proprietário.");
			}

		Optional<NominatimResponse> coordenadas = nominatimClient.buscarCoordenadas(local.getEndereco());
		if (coordenadas.isPresent()) {
			local.setLatitude(coordenadas.get().getLatitude());
			local.setLongitude(coordenadas.get().getLongitude());
		} else {
			local.setLatitude(0.0);
			local.setLongitude(0.0);
			System.err.print("Aviso: Endereço não geocodificado.");
		}

		Set<TagAcessibilidade> tagsExistentes = new HashSet<>(tagRepository.findAllById(idsTags));

		// Verifica se todas as tags solicitadas foram encontradas
		if (tagsExistentes.size() != idsTags.size()) {
			System.err.println("Aviso: Algumas Tags informadas não foram encontradas no banco de dados.");
		}

		Set<LocalTag> novoRelacionamentoTags = new HashSet<>();

		// Atribui a cada LocalTag o Local e a Tag, inicializando o score em 0.0
		for (TagAcessibilidade tag : tagsExistentes) {
			LocalTag localTag = new LocalTag(local, tag);

			novoRelacionamentoTags.add(localTag);
		}

		// Configurar o Local (incluindo o novo conjunto de LocalTag)
		local.setTagsComScore(novoRelacionamentoTags);

		// O Local precisa ser referenciado de volta no LocalTag para salvar
		// corretamente
		for (LocalTag lt : novoRelacionamentoTags) {
			lt.setLocal(local);
		}

		// Salva Local e os relacionamentos de LocalTag em cascata (Devido ao
		// CascadeType.ALL)
		return localRepository.save(local);
	}

	public List<Local> buscarTodos() {
		return localRepository.findAll();
	}
	
	public List<Local> buscarPorProprietario(Long proprietarioId) {
		System.out.println(">>> LocalService.buscarPorProprietario - ID: " + proprietarioId);
		List<Local> locais = localRepository.findByProprietarioId(proprietarioId);
		System.out.println(">>> Locais encontrados no repositório: " + locais.size());
		
		if (locais.isEmpty()) {
			System.out.println("⚠️ NENHUM LOCAL ENCONTRADO para proprietario_id = " + proprietarioId);
			System.out.println("    Execute o script SQL para associar locais ou cadastre novos locais pelo mapa");
		} else {
			for (Local local : locais) {
				System.out.println("    - Local ID " + local.getId() + ": " + local.getNome());
			}
		}
		
		return locais;
	}

	public Optional<Local> buscarPorId(Long id) {
		return localRepository.findById(id);
	}

	@Transactional
	public Local atualizarLocal(Long id, Local detalhesLocal, Set<Long> idsTags) {

		Local localExistente = localRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Local não encontrado com ID: " + id));

		// Atualiza campos básicos
		localExistente.setNome(detalhesLocal.getNome());
		localExistente.setDescricao(detalhesLocal.getDescricao());
		localExistente.setEndereco(detalhesLocal.getEndereco());

		// Atualiza coordenadas se o endereço mudou
		if (detalhesLocal.getEndereco() != null &&
			!detalhesLocal.getEndereco().equals(localExistente.getEndereco())) {

			Optional<NominatimResponse> coordenadas =
					nominatimClient.buscarCoordenadas(detalhesLocal.getEndereco());

			if (coordenadas.isPresent()) {
				localExistente.setLatitude(coordenadas.get().getLatitude());
				localExistente.setLongitude(coordenadas.get().getLongitude());
			}
		}

		// Atualiza tags APENAS se o front enviar
		if (idsTags != null) {
			Set<TagAcessibilidade> novasTags = new HashSet<>(tagRepository.findAllById(idsTags));
			Set<LocalTag> novoRelacionamento = new HashSet<>();

			for (TagAcessibilidade tag : novasTags) {
				LocalTag lt = new LocalTag(localExistente, tag);
				novoRelacionamento.add(lt);
			}

			localExistente.setTagsComScore(novoRelacionamento);
		}

		return localRepository.save(localExistente);
	}


	public void deletarLocal(Long id) {
		localRepository.deleteById(id);
	}

	/*
	 * ==================================================================== MÉTODO
	 * PARA O BE4 (Comentários) ATUALIZAR O SCORE DE CONFIANÇA
	 * ====================================================================
	 */

	/**
	 * Este método será chamado pelo Service do BE4 (Comentários) para recalcular o
	 * score de confiança de uma tag de um local após um novo feedback.
	 * 
	 * @param localId   ID do local avaliado.
	 * @param tagId     ID da tag que teve seu score alterado.
	 * @param novoScore Novo valor de confiança calculado pelo BE4.
	 */

	@Transactional
	public void atualizarConfiancaTag(Long localId, Long tagId, Double novoScore) {
		// Encontra o local
		Local local = localRepository.findById(localId)
				.orElseThrow(() -> new RuntimeException("Local não encontrado."));

		// Percorre as tagsComScore para encontrar a tag específica
		local.getTagsComScore().stream().filter(lt -> lt.getTag().getId().equals(tagId)).findFirst().ifPresent(lt -> {
			lt.setConfiancaScore(novoScore);
			// O save não é estritamente necessário se a transação for fechada (devido ao
			// @Transactional e dirty checking),
			// mas garante que a alteração será persistida.
			localRepository.save(local);

		});
	}

}
