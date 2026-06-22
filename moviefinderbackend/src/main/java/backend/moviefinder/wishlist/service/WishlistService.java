package backend.moviefinder.wishlist.service;

import backend.moviefinder.auth.model.User;
import backend.moviefinder.auth.repository.UserRepository;
import backend.moviefinder.wishlist.dto.WishlistMovieDto;
import backend.moviefinder.wishlist.model.WishlistMovie;
import backend.moviefinder.wishlist.repository.WishlistMovieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistMovieRepository wishlistRepository;
    private final UserRepository userRepository;

    public WishlistService(WishlistMovieRepository wishlistRepository, UserRepository userRepository) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
    }

    // 1. Agregar a la lista
    public void addMovie(String email, Long tmdbId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (wishlistRepository.existsByUserAndTmdbId(user, tmdbId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La película ya está en tu lista");
        }

        WishlistMovie movie = new WishlistMovie();
        movie.setUser(user);
        movie.setTmdbId(tmdbId);
        wishlistRepository.save(movie);
    }

    // 2. Eliminar de la lista
    public void removeMovie(String email, Long tmdbId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        WishlistMovie movie = wishlistRepository.findByUserAndTmdbId(user, tmdbId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La película no está en tu lista"));

        wishlistRepository.delete(movie);
    }

    // 3. Obtener la lista del usuario
    public List<WishlistMovieDto> getUserWishlist(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return wishlistRepository.findByUserOrderByAddedAtDesc(user)
                .stream()
                .map(movie -> {
                    WishlistMovieDto dto = new WishlistMovieDto();
                    dto.setTmdbId(movie.getTmdbId());
                    dto.setAddedAt(movie.getAddedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
