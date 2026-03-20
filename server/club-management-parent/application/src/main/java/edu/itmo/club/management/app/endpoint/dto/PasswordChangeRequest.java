package edu.itmo.club.management.app.endpoint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequest {

	@NotBlank
	private String oldPassword;

	@NotBlank
	@Size(min = 6, max = 128)
	private String newPassword;
}
