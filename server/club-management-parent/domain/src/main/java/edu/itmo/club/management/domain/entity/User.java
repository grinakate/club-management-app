package edu.itmo.club.management.domain.entity;

import edu.itmo.club.management.domain.enums.UserRole;
import edu.itmo.club.management.domain.enums.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Информация о пользователе.
 */
@Entity
@Getter
@Setter
@Table(name = "\"user\"")
public class User implements UserDetails {

	@Id
	@NonNull
	@Column(name = "user_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
	@SequenceGenerator(sequenceName = "user_seq", name = "user_sequence", allocationSize = 1)
	private Long id;

	@NonNull
	@Column(name = "full_name", nullable = false)
	private String fullName;

	@Column(name = "email")
	private String email;

	@Column(name = "phone")
	private String phone;

	@NonNull
	@Column(name = "password_hash", nullable = false)
	private String password;

	@Column(name = "city")
	private String city;

	@Column(name = "interests")
	private String interests;

	@NonNull
	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Column(name = "role", nullable = false)
	private UserRole role;

	@NonNull
	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Column(name = "status", nullable = false)
	private UserStatus status;

	@NonNull
	@Column(name = "birth_date", nullable = false)
	private LocalDate birthDate;

	@NonNull
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
	}

	@Override
	public String getUsername() {
		return email != null ? email : phone;
	}

	@Override
	public boolean isAccountNonExpired() {
		return status.equals(UserStatus.ACTIVE);
	}

	@Override
	public boolean isAccountNonLocked() {
		return status.equals(UserStatus.ACTIVE);
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return status.equals(UserStatus.ACTIVE);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof User user)) {
			return false;
		}
		return Objects.equals(getId(), user.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
