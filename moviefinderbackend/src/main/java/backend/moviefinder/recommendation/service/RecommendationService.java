package backend.moviefinder.recommendation.service;

import backend.moviefinder.recommendation.dto.ProviderRecommendationDto;
import backend.moviefinder.recommendation.dto.RecommendationResponseDto;
import backend.moviefinder.streaming.service.StreamingService;
import backend.moviefinder.wishlist.model.WishlistMovie;
import backend.moviefinder.wishlist.repository.WishlistMovieRepository;
import backend.moviefinder.auth.model.User;
import backend.moviefinder.auth.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final WishlistMovieRepository wishlistRepository;
    private final UserRepository userRepository;
    private final StreamingService streamingService;

    public RecommendationService(WishlistMovieRepository wishlistRepository,
                                  UserRepository userRepository,
                                  StreamingService streamingService) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.streamingService = streamingService;
    }

    public RecommendationResponseDto getRecommendations(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        List<WishlistMovie> wishlist = wishlistRepository.findByUserOrderByAddedAtDesc(user);

        if (wishlist.isEmpty()) {
            return new RecommendationResponseDto(0, List.of());
        }

        int totalMovies = wishlist.size();

        Map<String, Set<Long>> providerMovieMap = new HashMap<>();
        Map<String, String> providerPrimaryType = new HashMap<>();
        Map<String, Double> providerTotalCost = new HashMap<>();

        for (WishlistMovie movie : wishlist) {
            Long tmdbId = movie.getTmdbId();
            try {
                var sources = streamingService.getStreamingSources(tmdbId);

                for (var source : sources) {
                    String name = source.getName().trim();
                    String type = source.getType();

                    providerMovieMap.computeIfAbsent(name, k -> new HashSet<>()).add(tmdbId);

                    providerPrimaryType.merge(name, type, (existing, incoming) ->
                        priorityType(incoming, existing)
                    );

                    if (("rent".equals(type) || "buy".equals(type)) && source.getPrice() != null) {
                        providerTotalCost.merge(name, source.getPrice(), Double::sum);
                    }
                }
            } catch (Exception e) {
                // Si falla la consulta a Watchmode para una película, la saltamos
            }
        }

        List<ProviderRecommendationDto> recommendations = providerMovieMap.entrySet()
                .stream()
                .map(entry -> {
                    String providerName = entry.getKey();
                    int covered = entry.getValue().size();
                    double percentage = (double) covered / totalMovies * 100;
                    percentage = Math.round(percentage * 10.0) / 10.0;
                    String type = providerPrimaryType.getOrDefault(providerName, "unknown");
                    Double cost = providerTotalCost.get(providerName);
                    return new ProviderRecommendationDto(providerName, covered, totalMovies, percentage, type, cost);
                })
                .sorted((a, b) -> {
                    int cmp = Integer.compare(b.getCoveredMovies(), a.getCoveredMovies());
                    if (cmp != 0) return cmp;
                    if (a.getEstimatedCost() == null && b.getEstimatedCost() == null) return 0;
                    if (a.getEstimatedCost() == null) return -1;
                    if (b.getEstimatedCost() == null) return 1;
                    return Double.compare(a.getEstimatedCost(), b.getEstimatedCost());
                })
                .limit(3)
                .collect(Collectors.toList());

        return new RecommendationResponseDto(totalMovies, recommendations);
    }

    private String priorityType(String incoming, String existing) {
        int incomingP = typePriority(incoming);
        int existingP = typePriority(existing);
        return incomingP < existingP ? incoming : existing;
    }

    private int typePriority(String type) {
        return switch (type) {
            case "sub" -> 0;
            case "free" -> 1;
            case "rent" -> 2;
            case "buy" -> 3;
            default -> 4;
        };
    }
}
