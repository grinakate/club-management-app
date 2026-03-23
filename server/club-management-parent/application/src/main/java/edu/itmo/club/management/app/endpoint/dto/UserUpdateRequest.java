package edu.itmo.club.management.app.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {

	@Size(max = 150)
	private String fullName;

	@Email
	private String email;

	private String phone;

	private String city;

	private String interests;
}
