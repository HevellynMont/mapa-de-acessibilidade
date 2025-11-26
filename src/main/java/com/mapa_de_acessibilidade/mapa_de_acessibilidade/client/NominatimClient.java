package com.mapa_de_acessibilidade.mapa_de_acessibilidade.client;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus; // Import necessário
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException; // Import necessário

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.client.dto.NominatimResponse;

@Component
public class NominatimClient {

    private final RestTemplate restTemplate;

    public NominatimClient() {
        this.restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "MapaDeAcessibilidadeApp/1.0");

        headers.set("Referer", "http://localhost:8080/mapa-de-acessibilidade-api");

        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            request.getHeaders().addAll(headers);
            return execution.execute(request, body);
        };

        restTemplate.setInterceptors(Collections.singletonList(interceptor));
    }

    @Value("${nominatim.api.base-url}")
    private String nominatimBaseUrl;

    public Optional<NominatimResponse> buscarCoordenadas(String endereco) {

        String url = nominatimBaseUrl + "search?q=" + endereco + "&format=json&limit=1";

        NominatimResponse[] responses = restTemplate.getForObject(url, NominatimResponse[].class);

        System.out.println("DEBUG NOMINATIM: Array de resposta recebido. Tamanho: "
                + (responses != null ? responses.length : "null"));

        if (responses != null && responses.length > 0) {
            System.out.println("SUCESSO NOMINATIM: Coordenadas encontradas para: " + endereco);
            return Optional.of(responses[0]);
        } else {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Endereço não localizado no mapa: " + endereco);
        }
    }
}