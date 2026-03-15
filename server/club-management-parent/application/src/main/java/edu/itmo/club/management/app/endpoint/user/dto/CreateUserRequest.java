package edu.itmo.club.management.app.endpoint.user.dto;

import edu.itmo.club.management.domain.enums.UserRole;
import edu.itmo.club.management.domain.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Запрос для создания пользователя.
 * TODO: валидация входных параметров
 */
@Data
public class CreateUserRequest {

	@NotNull
	private String fullName;

	@Email
	private String email;

	private String phone;

	private String city;

	private String interests;

	@NotNull
	private UserRole role;

	@NotNull
	private UserStatus status;

	@NotNull
	private String password;
}
