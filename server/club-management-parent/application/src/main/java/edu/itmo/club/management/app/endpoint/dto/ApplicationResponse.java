package edu.itmo.club.management.app.endpoint.dto;

import edu.itmo.club.management.domain.enums.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationResponse {

	private Long id;
	private Long clubId;
	private String clubName;
	private Long userId;
	private String userFullName;
	private LocalDateTime appliedAt;
	private ApplicationStatus status;
	private String reviewedByFullName;
	private LocalDateTime reviewedAt;
	private String comment;
}
