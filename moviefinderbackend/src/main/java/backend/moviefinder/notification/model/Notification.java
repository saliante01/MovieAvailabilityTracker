package backend.moviefinder.notification.model;

import backend.moviefinder.auth.model.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A qué usuario le pertenece esta notificación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // El ID de la película que generó la alerta (para que al tocarla en Flutter, abra el detalle)
    @Column(name = "tmdb_id", nullable = false)
    private Long tmdbId;

    // Título corto (Ej. "¡Nueva plataforma!")
    @Column(nullable = false)
    private String title;

    // Mensaje detallado (Ej. "Interestelar ahora está en MAX")
    @Column(nullable = false)
    private String message;

    // Para saber si mostramos la campanita con un punto rojo o no
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
