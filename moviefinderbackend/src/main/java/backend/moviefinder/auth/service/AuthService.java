package backend.moviefinder.auth.service;

import backend.moviefinder.auth.dto.AuthResponseDTO;
import backend.moviefinder.auth.dto.LoginRequestDTO;
import backend.moviefinder.auth.dto.RegisterRequestDTO;
import backend.moviefinder.auth.dto.UserSummaryDTO;
import backend.moviefinder.auth.model.User;
import backend.moviefinder.auth.repository.UserRepository;
import backend.moviefinder.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already taken");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user);

        return AuthResponseDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getHandle())
                .token(token)
                .build();
    }

    @Transactional
    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        String token = jwtService.generateToken(user);

        return AuthResponseDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getHandle())
                .token(token)
                .build();
    }

    @Transactional(readOnly = true)
    public UserSummaryDTO getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return UserSummaryDTO.builder()
                .id(user.getId())
                .username(user.getHandle())
                .email(user.getEmail())
                .build();
    }
}