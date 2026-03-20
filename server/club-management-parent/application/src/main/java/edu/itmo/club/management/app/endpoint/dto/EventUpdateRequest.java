package edu.itmo.club.management.app.endpoint.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class EventUpdateRequest {

	@Size(max = 200)
	private String title;

	private String description;

	private LocalDateTime startAt;

	private LocalDateTime endAt;

	@Positive
	private Integer participantLimit;

	@PositiveOrZero
	private BigDecimal price;

	private String status;
}
