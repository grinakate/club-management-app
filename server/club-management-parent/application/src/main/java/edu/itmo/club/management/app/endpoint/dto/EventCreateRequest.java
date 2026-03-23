package edu.itmo.club.management.app.endpoint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class EventCreateRequest {

	@NotNull
	private Long clubId;

	@NotBlank
	@Size(max = 200)
	private String title;

	private String description;

	@NotNull
	private LocalDateTime startAt;

	@NotNull
	private LocalDateTime endAt;

	@Positive
	private Integer participantLimit;

	@PositiveOrZero
	private BigDecimal price;
}
