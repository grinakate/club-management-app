package edu.itmo.club.management.domain.entity;

import edu.itmo.club.management.domain.enums.ApplicationStatus;
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
@Table(name = "membership_application")
public class MembershipApplication {

	@Id
	@NonNull
	@Column(name = "application_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_sequence")
	@SequenceGenerator(sequenceName = "application_seq", name = "application_sequence", allocationSize = 1)
	private Long id;

	@NonNull
	@ManyToOne
	@JoinColumn(name = "club_id", nullable = false)
	private Club club;

	@NonNull
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "applied_at")
	private LocalDateTime appliedAt;

	@NonNull
	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Column(name = "status", nullable = false)
	private ApplicationStatus status;

	@ManyToOne
	@JoinColumn(name = "reviewed_by")
	private User reviewedBy;

	@Column(name = "reviewed_at")
	private LocalDateTime reviewedAt;

	@Column(name = "comment")
	private String comment;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof MembershipApplication that)) {
			return false;
		}
		return Objects.equals(getId(), that.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
