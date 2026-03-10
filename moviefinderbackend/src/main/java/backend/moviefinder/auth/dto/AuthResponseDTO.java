package backend.moviefinder.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {
    private Long userId;
    private String email;
    private String username;
    private String token;
}