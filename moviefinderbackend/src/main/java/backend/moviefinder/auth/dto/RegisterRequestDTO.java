package backend.moviefinder.auth.dto;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String username; // "MovieFan99"
    private String email;
    private String password;
}