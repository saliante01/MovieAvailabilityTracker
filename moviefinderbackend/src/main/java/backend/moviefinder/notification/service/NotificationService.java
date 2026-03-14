package backend.moviefinder.notification.service;

import backend.moviefinder.auth.model.User;
import backend.moviefinder.auth.repository.UserRepository;
import backend.moviefinder.notification.dto.NotificationDto;
import backend.moviefinder.notification.model.Notification;
import backend.moviefinder.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // 1. Obtener TODAS las notificaciones
    public List<NotificationDto> getUserNotifications(String email) {
        User user = getUser(email);
        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // 2. Obtener solo las NO LEÍDAS (para el contador rojo de la app)
    public List<NotificationDto> getUnreadNotifications(String email) {
        User user = getUser(email);
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user)
                .stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // 3. Marcar una notificación como leída
    public void markAsRead(Long notificationId, String email) {
        User user = getUser(email);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        // Validamos que la notificación realmente le pertenezca a este usuario
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permiso para modificar esta notificación");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // 4. Crear una nueva notificación (Lo usaremos internamente luego)
    public void createNotification(User user, Long tmdbId, String title, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTmdbId(tmdbId);
        notification.setTitle(title);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    // Funciones auxiliares privadas
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private NotificationDto convertToDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setTmdbId(notification.getTmdbId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
