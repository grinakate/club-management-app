package edu.itmo.club.management.app.endpoint.dto;

import edu.itmo.club.management.domain.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationReviewRequest {

	@NotNull
	private ApplicationStatus decision;

	@Size(max = 500)
	private String comment;
}
