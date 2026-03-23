package edu.itmo.club.management.domain.repository;

import edu.itmo.club.management.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByUserIdOrderByScheduledAtDesc(Long userId);

	long countByUserIdAndIsReadFalse(Long userId);

	@Modifying
	@Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
	void markAllAsReadByUserId(@Param("userId") Long userId);
}
