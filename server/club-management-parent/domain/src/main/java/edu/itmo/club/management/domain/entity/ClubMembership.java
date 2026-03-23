package edu.itmo.club.management.domain.entity;

import edu.itmo.club.management.domain.enums.MembershipRole;
import edu.itmo.club.management.domain.enums.MembershipStatus;
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
@Table(name = "club_membership", uniqueConstraints = @UniqueConstraint(columnNames = {"club_id", "user_id"}))
public class ClubMembership {

	@Id
	@NonNull
	@Column(name = "membership_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "membership_sequence")
	@SequenceGenerator(sequenceName = "membership_seq", name = "membership_sequence", allocationSize = 1)
	private Long id;

	@NonNull
	@ManyToOne
	@JoinColumn(name = "club_id", nullable = false)
	private Club club;

	@NonNull
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@NonNull
	@Column(name = "joined_at", nullable = false)
	private LocalDateTime joinedAt;

	@NonNull
	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Column(name = "member_role", nullable = false)
	private MembershipRole memberRole;

	@NonNull
	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Column(name = "status", nullable = false)
	private MembershipStatus status;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ClubMembership that)) {
			return false;
		}
		return Objects.equals(getId(), that.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
