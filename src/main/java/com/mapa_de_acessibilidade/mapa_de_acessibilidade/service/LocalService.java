package com.mapa_de_acessibilidade.mapa_de_acessibilidade.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.client.NominatimClient;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.dto.request.LocalRequestDTO;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Comentario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Local;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.Proprietario;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.model.enums.TagAcessibilidadeEnum;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.LocalRepository;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.repository.ProprietarioRepository;

@Service
public class LocalService {
    @Autowired
    private LocalRepository localRepo;
    @Autowired
    private ProprietarioRepository propRepo;
    @Autowired
    private NominatimClient nominatimClient;

    public Local salvar(Local local, Long idProprietario) {
        Proprietario dono = propRepo.findById(idProprietario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proprietário não encontrado"));
        local.setProprietario(dono);

        if (local.getId() == null || local.getLatitude() == null) {
            nominatimClient.buscarCoordenadas(local.getEndereco()).ifPresent(coords -> {
                local.setLatitude(coords.getLatitude());
                local.setLongitude(coords.getLongitude());
            });
        }
        return localRepo.save(local);
    }

    public List<Local> listar() {
        return localRepo.findAll();
    }

    public Local buscarPorId(Long id) {
        return localRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Local não encontrado"));
    }

    public Local atualizar(Long id, LocalRequestDTO dto) {
        Local local = buscarPorId(id);
        BeanUtils.copyProperties(dto, local, "idProprietario");
        nominatimClient.buscarCoordenadas(local.getEndereco()).ifPresent(coords -> {
            local.setLatitude(coords.getLatitude());
            local.setLongitude(coords.getLongitude());
        });

        return localRepo.save(local);
    }

    public void deletar(Long id) {
        if (!localRepo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Local não encontrado");
        localRepo.deleteById(id);
    }

    public void recalcularReputacao(Long idLocal) {
        Local local = buscarPorId(idLocal);
        Set<Comentario> comentarios = local.getComentarios();

        if (comentarios.isEmpty()) {
            local.setMediaAvaliacao(0.0);
            local.setPossuiSelo(false);
            localRepo.save(local);
            return;
        }

        // Conta quantas tags POSITIVAS existem no total no Enum
        long totalPossiveis = java.util.Arrays.stream(TagAcessibilidadeEnum.values())
                .filter(t -> !t.isNegativa())
                .count();
        
        if (totalPossiveis == 0) totalPossiveis = 1;

        double somaNotas = 0.0;
        int qtdComentariosValidos = 0;

        for (Comentario c : comentarios) {
            boolean temNegativa = c.getTags().stream().anyMatch(TagAcessibilidadeEnum::isNegativa);

            if (temNegativa) {
                somaNotas += 0.0;
            } else {
                long marcadas = c.getTags().size();
                double notaCalculada = ((double) marcadas / totalPossiveis) * 5.0;
                if (notaCalculada > 5.0) notaCalculada = 5.0;
                
                somaNotas += notaCalculada;
            }
            qtdComentariosValidos++;
        }

        double mediaFinal = qtdComentariosValidos > 0 ? (somaNotas / qtdComentariosValidos) : 0.0;
        mediaFinal = Math.round(mediaFinal * 10.0) / 10.0;

        local.setMediaAvaliacao(mediaFinal);
        local.setPossuiSelo(mediaFinal >= 3.0);

        localRepo.save(local);
    }
}