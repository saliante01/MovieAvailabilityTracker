package backend.moviefinder.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class RecommendationResponseDto {
    private int totalMovies;
    private List<ProviderRecommendationDto> recommendations;
}
