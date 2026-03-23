package edu.itmo.club.management.domain.entity;

import edu.itmo.club.management.domain.enums.DeliveryStatus;
import edu.itmo.club.management.domain.enums.NotificationChannel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "notification")
public class Notification {

	@Id
	@NonNull
	@Column(name = "notification_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_sequence")
	@SequenceGenerator(sequenceName = "notification_seq", name = "notification_sequence", allocationSize = 1)
	private Long id;

	@NonNull
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "event_id")
	private Event event;

	@ManyToOne
	@JoinColumn(name = "club_id")
	private Club club;

	@NonNull
	@Column(name = "subject", nullable = false)
	private String subject;

	@Column(name = "message_text")
	private String messageText;

	@NonNull
	@Column(name = "scheduled_at", nullable = false)
	private LocalDateTime scheduledAt;

	@Column(name = "sent_at")
	private LocalDateTime sentAt;

	@NonNull
	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Column(name = "channel", nullable = false)
	private NotificationChannel channel;

	@NonNull
	@Column(name = "is_read", nullable = false)
	private Boolean isRead;

	@NonNull
	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Column(name = "delivery_status", nullable = false)
	private DeliveryStatus deliveryStatus;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Notification that)) {
			return false;
		}
		return Objects.equals(getId(), that.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
