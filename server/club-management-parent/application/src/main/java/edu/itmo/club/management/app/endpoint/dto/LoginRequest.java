package edu.itmo.club.management.app.endpoint.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class LoginRequest {

	@NotNull
	private String login;

	@NotNull
	private String password;
}
