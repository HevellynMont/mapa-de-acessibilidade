package com.mapa_de_acessibilidade.mapa_de_acessibilidade.client;

import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.client.dto.NominatimResponseDTO;

@Component
public class NominatimClient {

    private static final Logger logger = LoggerFactory.getLogger(NominatimClient.class);
    private final RestTemplate restTemplate;

    @Value("${nominatim.api.base-url}")
    private String nominatimBaseUrl;

    public NominatimClient() {
        this.restTemplate = new RestTemplate();

        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            request.getHeaders().add(HttpHeaders.USER_AGENT, "MapaAcessibilidadeApp/1.0 (contato@email.com)"); // prod
            request.getHeaders().add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            return execution.execute(request, body);
        };

        this.restTemplate.setInterceptors(Collections.singletonList(interceptor));
    }

    public Optional<NominatimResponseDTO> buscarCoordenadas(String endereco) {
        try {
            String url = UriComponentsBuilder.fromUriString(nominatimBaseUrl)
                    .path("search")
                    .queryParam("q", endereco)
                    .queryParam("format", "json")
                    .queryParam("limit", 1)
                    .toUriString();

            logger.info("Buscando coordenadas para: {}", endereco);

            NominatimResponseDTO[] responses = restTemplate.getForObject(url, NominatimResponseDTO[].class);

            if (responses != null && responses.length > 0) {
                logger.info("Coordenadas encontradas: Lat {}, Lon {}", responses[0].getLatitude(),
                        responses[0].getLongitude());
                return Optional.of(responses[0]);
            } else {
                logger.warn("Endereço não encontrado no Nominatim: {}", endereco);
                return Optional.empty();
            }

        } catch (Exception e) {
            logger.error("Erro ao conectar com Nominatim API: {}", e.getMessage());
            return Optional.empty();
        }
    }
}