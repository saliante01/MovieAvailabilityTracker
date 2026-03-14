package backend.moviefinder.streaming.service;

import backend.moviefinder.core.exceptions.ExternalApiException;
import backend.moviefinder.streaming.dto.WatchmodeSourceDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Service
public class StreamingService {

    private final RestTemplate restTemplate;

    @Value("${watchmode.api.url}")
    private String watchmodeApiUrl;

    @Value("${watchmode.api.key}")
    private String watchmodeApiKey;

    public StreamingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<WatchmodeSourceDto> getStreamingSources(Long tmdbId) {
        try {
            // Aquí está el truco: agregamos "movie-" al ID de TMDB
            String watchmodeId = "movie-" + tmdbId;

            String url = UriComponentsBuilder.fromUriString(watchmodeApiUrl + "/title/" + watchmodeId + "/sources/")
                    .queryParam("apiKey", watchmodeApiKey)
                    .toUriString();

            // Watchmode devuelve un arreglo directo, así que usamos un Array y lo convertimos a Lista
            WatchmodeSourceDto[] responseArray = restTemplate.getForObject(url, WatchmodeSourceDto[].class);

            if (responseArray == null) {
                return List.of(); // Devolvemos lista vacía si no hay resultados
            }

            return Arrays.asList(responseArray);

        } catch (RestClientException e) {
            throw new ExternalApiException("Error al conectar con Watchmode: " + e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}