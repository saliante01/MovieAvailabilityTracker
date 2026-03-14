package backend.moviefinder.core.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada para manejar errores al comunicarse con servicios como TMDB o Watchmode.
 */
public class ExternalApiException extends RuntimeException {

    private final HttpStatus status;

    public ExternalApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
