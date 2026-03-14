package backend.moviefinder.catalog.service;

import backend.moviefinder.catalog.dto.TmdbMovieDetailsDto;
import backend.moviefinder.catalog.dto.TmdbSearchResponseDto;
import backend.moviefinder.core.exceptions.ExternalApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CatalogService {

    private final RestTemplate restTemplate;

    // Inyectamos las variables de tu application.properties
    @Value("${tmdb.api.url}")
    private String tmdbApiUrl;

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    public CatalogService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public TmdbSearchResponseDto searchMovies(String query, int page) {
        try {
            String url = UriComponentsBuilder.fromUriString(tmdbApiUrl + "/search/movie")
                    .queryParam("api_key", tmdbApiKey)
                    .queryParam("query", query)
                    .queryParam("language", "es-ES")
                    .queryParam("page", page)
                    .toUriString();

            return restTemplate.getForObject(url, TmdbSearchResponseDto.class);

        } catch (RestClientException e) {
            throw new ExternalApiException("Error al conectar con TMDB: " + e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
    // Nuevo metodo para obtener los detalles de una sola película
    public TmdbMovieDetailsDto getMovieDetails(Long tmdbId) {
        try {
            // La URL ahora apunta a /movie/{id} en lugar de /search/movie
            String url = UriComponentsBuilder.fromUriString(tmdbApiUrl + "/movie/" + tmdbId)
                    .queryParam("api_key", tmdbApiKey)
                    .queryParam("language", "es-ES") // En español
                    .toUriString();

            return restTemplate.getForObject(url, TmdbMovieDetailsDto.class);

        } catch (RestClientException e) {
            throw new ExternalApiException("Error al obtener detalles de TMDB: " + e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}