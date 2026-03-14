package backend.moviefinder.notification.controller;

import backend.moviefinder.auth.model.User;
import backend.moviefinder.notification.service.NotificationService;
import backend.moviefinder.wishlist.model.WishlistMovie;
import backend.moviefinder.wishlist.repository.WishlistMovieRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final WishlistMovieRepository wishlistRepository;
    private final NotificationService notificationService;

    public DemoController(WishlistMovieRepository wishlistRepository, NotificationService notificationService) {
        this.wishlistRepository = wishlistRepository;
        this.notificationService = notificationService;
    }

    // Endpoint mágico para simular un cambio de plataforma
    @PostMapping("/simulate-change/{tmdbId}")
    public ResponseEntity<String> simulatePlatformChange(
            @PathVariable Long tmdbId,
            @RequestParam String newPlatform,
            @RequestParam String movieName) {

        // 1. Buscamos todas las entradas en TODAS las wishlists que tengan esta película
        // (En la vida real usaríamos un repositorio especializado para buscar por tmdbId sin importar el usuario)
        List<WishlistMovie> usersWithMovie = wishlistRepository.findAll().stream()
                .filter(movie -> movie.getTmdbId().equals(tmdbId))
                .toList();

        if (usersWithMovie.isEmpty()) {
            return ResponseEntity.badRequest().body("Ningún usuario tiene la película con ID " + tmdbId + " en su Wishlist.");
        }

        // 2. Por cada usuario que la tenga guardada, le enviamos una notificación
        int notificationsSent = 0;
        for (WishlistMovie wishlistEntry : usersWithMovie) {
            User user = wishlistEntry.getUser();

            String title = "¡Nueva plataforma disponible!";
            String message = "La película '" + movieName + "' ahora está disponible en " + newPlatform + ".";

            notificationService.createNotification(user, tmdbId, title, message);
            notificationsSent++;
        }

        return ResponseEntity.ok("Simulación exitosa. Se enviaron " + notificationsSent + " notificaciones.");
    }
}
