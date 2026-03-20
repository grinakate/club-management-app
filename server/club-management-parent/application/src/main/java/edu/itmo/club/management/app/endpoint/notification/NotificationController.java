package edu.itmo.club.management.app.endpoint.notification;

import edu.itmo.club.management.app.endpoint.dto.NotificationResponse;
import edu.itmo.club.management.app.mapper.NotificationMapper;
import edu.itmo.club.management.domain.entity.User;
import edu.itmo.club.management.service.business.notification.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

	private final NotificationService notificationService;
	private final NotificationMapper notificationMapper;

	@GetMapping
	public List<NotificationResponse> getAll(@AuthenticationPrincipal User user) {
		return notificationMapper.mapToResponse(notificationService.findByUserId(user.getId()));
	}

	@GetMapping("/unread-count")
	public Map<String, Long> getUnreadCount(@AuthenticationPrincipal User user) {
		return Map.of("count", notificationService.countUnread(user.getId()));
	}

	@PutMapping("/{id}/read")
	public NotificationResponse markAsRead(@AuthenticationPrincipal User user, @PathVariable Long id) {
		return notificationMapper.mapToResponse(notificationService.markAsRead(id, user.getId()));
	}

	@PutMapping("/read-all")
	public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal User user) {
		notificationService.markAllAsRead(user.getId());
		return ResponseEntity.noContent().build();
	}
}
