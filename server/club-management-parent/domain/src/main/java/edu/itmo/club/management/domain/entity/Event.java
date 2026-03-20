package edu.itmo.club.management.domain.entity;

import edu.itmo.club.management.domain.enums.EventStatus;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "event")
public class Event {

	@Id
	@NonNull
	@Column(name = "event_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_sequence")
	@SequenceGenerator(sequenceName = "event_seq", name = "event_sequence", allocationSize = 1)
	private Long id;

	@NonNull
	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Column(name = "status", nullable = false)
	private EventStatus status;

	@NonNull
	@ManyToOne
	@JoinColumn(name = "club_id", nullable = false)
	private Club club;

	@NonNull
	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "description")
	private String description;

	@NonNull
	@Column(name = "start_at", nullable = false)
	private LocalDateTime startAt;

	@NonNull
	@Column(name = "end_at", nullable = false)
	private LocalDateTime endAt;

	@Column(name = "participant_limit")
	private Integer participantLimit;

	@Column(name = "price")
	private BigDecimal price;

	@NonNull
	@ManyToOne
	@JoinColumn(name = "created_by", nullable = false)
	private User createdBy;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Event event)) {
			return false;
		}
		return Objects.equals(getId(), event.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
