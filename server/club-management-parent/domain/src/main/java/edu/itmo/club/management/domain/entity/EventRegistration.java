package edu.itmo.club.management.domain.entity;

import edu.itmo.club.management.domain.enums.RegistrationStatus;
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
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "event_registration", uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "user_id"}))
public class EventRegistration {

	@Id
	@NonNull
	@Column(name = "registration_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_registration_sequence")
	@SequenceGenerator(sequenceName = "event_registration_seq", name = "event_registration_sequence", allocationSize = 1)
	private Long id;

	@NonNull
	@ManyToOne
	@JoinColumn(name = "event_id", nullable = false)
	private Event event;

	@NonNull
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@NonNull
	@Column(name = "registered_at", nullable = false)
	private LocalDateTime registeredAt;

	@NonNull
	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Column(name = "status", nullable = false)
	private RegistrationStatus status;

	@Column(name = "attendance_mark")
	private Boolean attendanceMark;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof EventRegistration that)) {
			return false;
		}
		return Objects.equals(getId(), that.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
