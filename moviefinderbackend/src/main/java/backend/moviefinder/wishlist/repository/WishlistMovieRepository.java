package backend.moviefinder.wishlist.repository;

import backend.moviefinder.auth.model.User;
import backend.moviefinder.wishlist.model.WishlistMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistMovieRepository extends JpaRepository<WishlistMovie, Long> {

    List<WishlistMovie> findByUserOrderByAddedAtDesc(User user);

    Optional<WishlistMovie> findByUserAndTmdbId(User user, Long tmdbId);

    boolean existsByUserAndTmdbId(User user, Long tmdbId);
}
