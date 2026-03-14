package backend.moviefinder.auth.controller;

import backend.moviefinder.auth.dto.AuthResponseDTO;
import backend.moviefinder.auth.dto.LoginRequestDTO;
import backend.moviefinder.auth.dto.RegisterRequestDTO;
import backend.moviefinder.auth.dto.UserSummaryDTO;
import backend.moviefinder.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private static final long COOKIE_EXPIRY = 24 * 60 * 60; // 24 horas

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request, HttpServletResponse response) {
        AuthResponseDTO result = authService.register(request);
        setCookie(response, result.getToken());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request, HttpServletResponse response) {
        AuthResponseDTO result = authService.login(request);
        setCookie(response, result.getToken());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<UserSummaryDTO> getMe() {
        return ResponseEntity.ok(authService.getAuthenticatedUser());
    }

    private void setCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("accessCookie", token)
                .httpOnly(true)
                .secure(false) // Poner 'true' si: HTTPS en producción
                .path("/")
                .maxAge(COOKIE_EXPIRY)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}