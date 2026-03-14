package backend.moviefinder.notification.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDto {
    private Long id;
    private Long tmdbId;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
}
