package backend.moviefinder.wishlist.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WishlistMovieDto {
    private Long tmdbId;
    private LocalDateTime addedAt;
}
