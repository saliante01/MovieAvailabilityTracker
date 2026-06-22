package backend.moviefinder.streaming.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WatchmodeSourceDto {

    @JsonProperty("source_id")
    private Integer sourceId;

    // Nombre de la plataforma (ej. "Netflix", "MAX")
    private String name;

    // Tipo de acceso (ej. "sub" para suscripción, "rent", "buy", "free")
    private String type;

    // Región de la plataforma (ej. "US", "CL", "BR")
    private String region;

    // El link directo para ver la película
    @JsonProperty("web_url")
    private String webUrl;

    private String format;

    private Double price;
}