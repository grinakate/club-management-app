package edu.itmo.club.management.domain.entity;

import edu.itmo.club.management.domain.enums.ClubStatus;
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
@Table(name = "club")
public class Club {

	@Id
	@NonNull
	@Column(name = "club_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "club_sequence")
	@SequenceGenerator(sequenceName = "club_seq", name = "club_sequence", allocationSize = 1)
	private Long id;

	@NonNull
	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description")
	private String description;

	@NonNull
	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@Column(name = "age_limit_min")
	private Integer ageLimitMin;

	@Column(name = "age_limit_max")
	private Integer ageLimitMax;

	@Column(name = "membership_fee")
	private BigDecimal membershipFee;

	@NonNull
	@ManyToOne
	@JoinColumn(name = "owner_user_id", nullable = false)
	private User owner;

	@NonNull
	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Column(name = "status", nullable = false)
	private ClubStatus status;

	@NonNull
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Club club)) {
			return false;
		}
		return Objects.equals(getId(), club.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
