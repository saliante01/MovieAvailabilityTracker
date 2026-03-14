package backend.moviefinder.notification.repository;

import backend.moviefinder.auth.model.User;
import backend.moviefinder.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 1. Historial completo: Trae todas las notificaciones del usuario ordenadas por fecha
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    // 2. Pendientes: Trae solo las notificaciones que no han sido leídas
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
}
