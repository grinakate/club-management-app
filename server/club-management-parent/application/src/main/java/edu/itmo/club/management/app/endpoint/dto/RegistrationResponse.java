package edu.itmo.club.management.app.endpoint.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RegistrationResponse {

	private Long id;
	private Long eventId;
	private String eventTitle;
	private Long userId;
	private String userFullName;
	private LocalDateTime registeredAt;
	private String status;
	private Boolean attendanceMark;
}
