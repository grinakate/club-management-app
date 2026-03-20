package edu.itmo.club.management.app.endpoint.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

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
	private LocalDate birthDate;

	@NotNull
	private String password;
}
