package backend.moviefinder.wishlist.controller;

import backend.moviefinder.wishlist.dto.WishlistMovieDto;
import backend.moviefinder.wishlist.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping("/{tmdbId}")
    public ResponseEntity<String> addMovieToWishlist(@PathVariable Long tmdbId, Principal principal) {
        // principal.getName() nos da el email del usuario logueado gracias al JWT
        wishlistService.addMovie(principal.getName(), tmdbId);
        return ResponseEntity.ok("Película agregada a tu lista.");
    }

    @DeleteMapping("/{tmdbId}")
    public ResponseEntity<String> removeMovieFromWishlist(@PathVariable Long tmdbId, Principal principal) {
        wishlistService.removeMovie(principal.getName(), tmdbId);
        return ResponseEntity.ok("Película eliminada de tu lista.");
    }

    @GetMapping
    public ResponseEntity<List<WishlistMovieDto>> getWishlist(Principal principal) {
        List<WishlistMovieDto> wishlist = wishlistService.getUserWishlist(principal.getName());
        return ResponseEntity.ok(wishlist);
    }
}
