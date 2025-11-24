package com.mapa_de_acessibilidade.mapa_de_acessibilidade.client;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mapa_de_acessibilidade.mapa_de_acessibilidade.client.dto.NominatimResponse;

// @Component registra a classe no Spring como um componente injetável
@Component
public class NominatimClient {

    private final RestTemplate restTemplate;

    public NominatimClient() {
        this.restTemplate = new RestTemplate();

        // Configura o User-Agent
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "MapaDeAcessibilidadeApp/1.0");

        // Isso é uma exigência de boas práticas do Nominatim!
        headers.set("Referer", "http://localhost:8080/mapa-de-acessibilidade-api");

        // 1. Cria o Interceptor como uma variável tipada
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            // Esta é a lógica que injeta o Header
            request.getHeaders().addAll(headers);
            return execution.execute(request, body);
        };

        // 2. Passa o Interceptor para o RestTemplate
        // Agora o compilador sabe exatamente o que está na lista.
        restTemplate.setInterceptors(Collections.singletonList(interceptor));

    }

    // Injeta o valor do application.properties
    @Value("${nominatim.api.base-url}")
    private String nominatimBaseUrl;

    /**
     * 
     * @param endereco Endereço completo em texto.
     * @return
     */
    public Optional<NominatimResponse> buscarCoordenadas(String endereco) {

        // 1. Codificação: Obriga o endereço a ser formatado para URL (ex: "Rua A" ->
        // "Rua%20A")

        // 2. Montagem da URL: Query para buscar o endereço e pedir o retorno em JSON
        // com apenas 1 resultado
        String url = nominatimBaseUrl + "search?q=" + endereco +
                "&format=json&limit=1";

        // 3. Execução: Faz a chamada GET e tenta mapear o array JSON para um array Java
        NominatimResponse[] responses = restTemplate.getForObject(url, NominatimResponse[].class);

        // Adicione este log para ver o que o RestTemplate recebeu:
        System.out.println("DEBUG NOMINATIM: Array de resposta recebido. Tamanho: "
                + (responses != null ? responses.length : "null"));

        // 4. Verificação: A API retorna um array vazio se não encontrar nada
        if (responses != null && responses.length > 0) {
            // Adicione esta linha:
            System.out.println("SUCESSO NOMINATIM: Coordenadas encontradas para: " + endereco);
            return Optional.of(responses[0]);
        } else {
            // Se o Nominatim retornou JSON vazio (não encontrou o endereço)
            System.err.println("AVISO NOMINATIM: Endereço não encontrado ou JSON vazio.");
        }

        // Retorna Optional.empty() se não encontrou ou se houve erro
        return Optional.empty();
    }
}