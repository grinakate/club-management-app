package edu.itmo.club.management.domain.repository;

import edu.itmo.club.management.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByUserIdOrderByScheduledAtDesc(Long userId);

	long countByUserIdAndIsReadFalse(Long userId);
}
