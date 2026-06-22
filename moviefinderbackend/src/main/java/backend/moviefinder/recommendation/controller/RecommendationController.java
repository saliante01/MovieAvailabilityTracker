package backend.moviefinder.recommendation.controller;

import backend.moviefinder.recommendation.dto.RecommendationResponseDto;
import backend.moviefinder.recommendation.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<RecommendationResponseDto> getRecommendations(Principal principal) {
        RecommendationResponseDto result = recommendationService.getRecommendations(principal.getName());
        return ResponseEntity.ok(result);
    }
}
