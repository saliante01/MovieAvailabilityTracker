package backend.moviefinder.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProviderRecommendationDto {
    private String providerName;
    private int coveredMovies;
    private int totalMovies;
    private double coveragePercentage;
    private String sourceType;
    private Double estimatedCost;
}
