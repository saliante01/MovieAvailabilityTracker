package backend.moviefinder.catalog.service;

import backend.moviefinder.catalog.dto.TmdbMovieDetailsDto;
import backend.moviefinder.catalog.dto.TmdbSearchResponseDto;
import backend.moviefinder.catalog.dto.TmdbTvResponseDto;
import backend.moviefinder.core.exceptions.ExternalApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

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

    private static final Map<String, String> MOVIE_CATEGORIES = Map.of(
        "popular", "/movie/popular",
        "top_rated", "/movie/top_rated",
        "upcoming", "/movie/upcoming",
        "now_playing", "/movie/now_playing"
    );

    private static final Map<String, String> TV_CATEGORIES = Map.of(
        "popular", "/tv/popular",
        "top_rated", "/tv/top_rated",
        "on_the_air", "/tv/on_the_air",
        "airing_today", "/tv/airing_today"
    );

    public TmdbSearchResponseDto browseMovies(String category, int page) {
        String endpoint = MOVIE_CATEGORIES.get(category);
        if (endpoint == null) {
            throw new ExternalApiException("Categoría de película no válida: " + category, HttpStatus.BAD_REQUEST);
        }
        return fetchTmdbList(endpoint, TmdbSearchResponseDto.class, page);
    }

    public TmdbTvResponseDto browseTv(String category, int page) {
        String endpoint = TV_CATEGORIES.get(category);
        if (endpoint == null) {
            throw new ExternalApiException("Categoría de TV no válida: " + category, HttpStatus.BAD_REQUEST);
        }
        return fetchTmdbList(endpoint, TmdbTvResponseDto.class, page);
    }

    public TmdbSearchResponseDto getTrendingMovies(String timeWindow, int page) {
        if (!timeWindow.equals("day") && !timeWindow.equals("week")) {
            throw new ExternalApiException("Periodo no válido: " + timeWindow, HttpStatus.BAD_REQUEST);
        }
        return fetchTmdbList("/trending/movie/" + timeWindow, TmdbSearchResponseDto.class, page);
    }

    public TmdbTvResponseDto getTrendingTv(String timeWindow, int page) {
        if (!timeWindow.equals("day") && !timeWindow.equals("week")) {
            throw new ExternalApiException("Periodo no válido: " + timeWindow, HttpStatus.BAD_REQUEST);
        }
        return fetchTmdbList("/trending/tv/" + timeWindow, TmdbTvResponseDto.class, page);
    }

    private <T> T fetchTmdbList(String path, Class<T> responseType, int page) {
        try {
            String url = UriComponentsBuilder.fromUriString(tmdbApiUrl + path)
                    .queryParam("api_key", tmdbApiKey)
                    .queryParam("language", "es-ES")
                    .queryParam("page", page)
                    .toUriString();
            return restTemplate.getForObject(url, responseType);
        } catch (RestClientException e) {
            throw new ExternalApiException("Error al conectar con TMDB: " + e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}