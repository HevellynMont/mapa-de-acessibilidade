package com.mapa_de_acessibilidade.mapa_de_acessibilidade.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.client.NominatimClient;
import com.mapa_de_acessibilidade.mapa_de_acessibilidade.client.dto.NominatimResponseDTO;
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
        Optional<Proprietario> donoOpt = propRepo.findById(idProprietario);
        if (donoOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proprietário não encontrado");
        }

        Proprietario dono = donoOpt.get();
        local.setProprietario(dono);

        if (local.getId() == null || local.getLatitude() == null) {
            Optional<NominatimResponseDTO> coordsOpt = nominatimClient.buscarCoordenadas(local.getEndereco());
            if (coordsOpt.isPresent()) {
                NominatimResponseDTO coords = coordsOpt.get();
                local.setLatitude(coords.getLatitude());
                local.setLongitude(coords.getLongitude());
            }
        }
        return localRepo.save(local);
    }

    public List<Local> listar() {
        return localRepo.findAll();
    }

    public Local buscarPorId(Long id) {
        Optional<Local> localOpt = localRepo.findById(id);
        if (localOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Local não encontrado");
        }
        return localOpt.get();
    }

    public Local atualizar(Long id, LocalRequestDTO dto) {
        Local local = buscarPorId(id);
        BeanUtils.copyProperties(dto, local, "idProprietario");

        Optional<NominatimResponseDTO> coordsOpt = nominatimClient.buscarCoordenadas(local.getEndereco());
        if (coordsOpt.isPresent()) {
            NominatimResponseDTO coords = coordsOpt.get();
            local.setLatitude(coords.getLatitude());
            local.setLongitude(coords.getLongitude());
        }

        return localRepo.save(local);
    }

    public void deletar(Long id) {
        if (!localRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Local não encontrado");
        }
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

        long totalPossiveis = 0;
        for (TagAcessibilidadeEnum t : TagAcessibilidadeEnum.values()) {
            if (!t.isNegativa()) {
                totalPossiveis++;
            }
        }

        if (totalPossiveis == 0)
            totalPossiveis = 1;

        double somaNotas = 0.0;
        int qtdComentariosValidos = 0;

        for (Comentario c : comentarios) {
            boolean temNegativa = false;

            for (TagAcessibilidadeEnum tagDoComentario : c.getTags()) {
                if (tagDoComentario.isNegativa()) {
                    temNegativa = true;
                    break;
                }
            }

            if (temNegativa) {
                somaNotas += 0.0;
            } else {
                long marcadas = c.getTags().size();
                double notaCalculada = ((double) marcadas / totalPossiveis) * 5.0;

                if (notaCalculada > 5.0)
                    notaCalculada = 5.0;
                somaNotas += notaCalculada;
            }
            qtdComentariosValidos++;
        }

        double mediaFinal = 0.0;
        if (qtdComentariosValidos > 0) {
            mediaFinal = somaNotas / qtdComentariosValidos;
        }

        mediaFinal = Math.round(mediaFinal * 10.0) / 10.0;
        local.setMediaAvaliacao(mediaFinal);

        if (mediaFinal >= 3.0) {
            local.setPossuiSelo(true);
        } else {
            local.setPossuiSelo(false);
        }

        localRepo.save(local);
    }
}