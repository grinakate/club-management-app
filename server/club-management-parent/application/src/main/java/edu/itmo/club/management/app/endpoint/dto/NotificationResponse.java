package edu.itmo.club.management.app.endpoint.dto;

import edu.itmo.club.management.domain.enums.DeliveryStatus;
import edu.itmo.club.management.domain.enums.NotificationChannel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponse {

	private Long id;
	private Long userId;
	private Long eventId;
	private Long clubId;
	private String subject;
	private String messageText;
	private NotificationChannel channel;
	private LocalDateTime scheduledAt;
	private LocalDateTime sentAt;
	private DeliveryStatus deliveryStatus;
	private Boolean isRead;
}
