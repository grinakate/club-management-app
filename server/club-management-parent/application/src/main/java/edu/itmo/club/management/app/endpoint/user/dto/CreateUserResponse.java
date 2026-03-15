package edu.itmo.club.management.app.endpoint.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ответ на запрос создания нового пользователя.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserResponse {

	@NotNull
	private Long id;
}
