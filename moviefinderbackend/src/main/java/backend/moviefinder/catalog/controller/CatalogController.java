package backend.moviefinder.catalog.controller;

import backend.moviefinder.catalog.dto.TmdbMovieDetailsDto;
import backend.moviefinder.catalog.dto.TmdbSearchResponseDto;
import backend.moviefinder.catalog.dto.TmdbTvResponseDto;
import backend.moviefinder.catalog.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/search")
    public ResponseEntity<TmdbSearchResponseDto> searchMovies(
            @RequestParam String query,
            // Agregamos el parámetro con valor por defecto
            @RequestParam(defaultValue = "1") int page) {

        // Le pasamos la query y la página al servicio
        TmdbSearchResponseDto response = catalogService.searchMovies(query, page);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/details/{tmdbId}")
    public ResponseEntity<TmdbMovieDetailsDto> getMovieDetails(@PathVariable Long tmdbId) {
        TmdbMovieDetailsDto details = catalogService.getMovieDetails(tmdbId);
        return ResponseEntity.ok(details);
    }

    @GetMapping("/movie/{category}")
    public ResponseEntity<TmdbSearchResponseDto> browseMovies(
            @PathVariable String category,
            @RequestParam(defaultValue = "1") int page) {
        TmdbSearchResponseDto response = catalogService.browseMovies(category, page);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tv/{category}")
    public ResponseEntity<TmdbTvResponseDto> browseTv(
            @PathVariable String category,
            @RequestParam(defaultValue = "1") int page) {
        TmdbTvResponseDto response = catalogService.browseTv(category, page);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/trending/movie")
    public ResponseEntity<TmdbSearchResponseDto> trendingMovies(
            @RequestParam(defaultValue = "week") String timeWindow,
            @RequestParam(defaultValue = "1") int page) {
        TmdbSearchResponseDto response = catalogService.getTrendingMovies(timeWindow, page);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/trending/tv")
    public ResponseEntity<TmdbTvResponseDto> trendingTv(
            @RequestParam(defaultValue = "week") String timeWindow,
            @RequestParam(defaultValue = "1") int page) {
        TmdbTvResponseDto response = catalogService.getTrendingTv(timeWindow, page);
        return ResponseEntity.ok(response);
    }
}