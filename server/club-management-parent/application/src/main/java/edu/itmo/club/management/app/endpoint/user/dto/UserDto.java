package edu.itmo.club.management.app.endpoint.user.dto;

import edu.itmo.club.management.domain.enums.UserRole;
import edu.itmo.club.management.domain.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * ДТО с информацией о пользователе.
 */
@Data
public class UserDto {

	@NotNull
	private Long id;

	@NotNull
	private String fullName;

	private String email;

	private String phone;

	private String city;

	private String interests;

	@NotNull
	private UserRole role;

	@NotNull
	private UserStatus status;
}
