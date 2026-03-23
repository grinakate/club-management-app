package edu.itmo.club.management.app.endpoint.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class EventResponse {

	private Long id;
	private Long clubId;
	private String clubName;
	private String title;
	private String description;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private Integer participantLimit;
	private BigDecimal price;
	private String status;
	private Long createdBy;
	private String createdByFullName;
}
