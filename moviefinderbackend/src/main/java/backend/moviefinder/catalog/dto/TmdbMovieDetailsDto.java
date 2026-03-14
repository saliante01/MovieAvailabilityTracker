package backend.moviefinder.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class TmdbMovieDetailsDto {
    private Long id;
    private String title;
    private String overview;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("release_date")
    private String releaseDate;

    // ¡Nuevos campos específicos del detalle!
    private Integer runtime; // Duración en minutos

    @JsonProperty("vote_average")
    private Double voteAverage; // Calificación (ej. 8.4)

    private List<TmdbGenreDto> genres; // Lista con los nombres de los géneros
}