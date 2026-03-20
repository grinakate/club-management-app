package edu.itmo.club.management.app.endpoint.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationCreateRequest {

	@Size(max = 500)
	private String comment;
}
