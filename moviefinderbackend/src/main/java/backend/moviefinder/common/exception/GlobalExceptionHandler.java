package backend.moviefinder.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja los errores de validación (@Valid) en los DTOs.
     * Ejemplo: "El email es obligatorio".
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Maneja las excepciones específicas de Spring (404 Not Found, 401 Unauthorized, etc.)
     * lanzadas desde el AuthService.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of("error", ex.getReason()));
    }

    /**
     * Maneja cualquier otro error inesperado (RuntimeException) como un 500 o 400 genérico.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        // Si por casualidad entra una ResponseStatusException aquí, la dejamos pasar
        if (ex instanceof ResponseStatusException) {
            return handleResponseStatusException((ResponseStatusException) ex);
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // O BAD_REQUEST según prefieras
                .body(Map.of("error", ex.getMessage() != null ? ex.getMessage() : "Error inesperado"));
    }
    /**
     * Maneja los errores provenientes de APIs externas (TMDB, Watchmode)
     */
    @ExceptionHandler(backend.moviefinder.core.exceptions.ExternalApiException.class)
    public ResponseEntity<Map<String, String>> handleExternalApiException(backend.moviefinder.core.exceptions.ExternalApiException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(Map.of("error", "Error en servicio externo: " + ex.getMessage()));
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(org.springframework.dao.DataIntegrityViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // Status 409 Conflict
                .body(Map.of("error", "Error de base de datos: Es posible que este registro (como el email) ya exista."));
    }
}