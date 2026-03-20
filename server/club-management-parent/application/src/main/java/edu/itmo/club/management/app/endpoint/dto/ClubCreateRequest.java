package edu.itmo.club.management.app.endpoint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ClubCreateRequest {

	@NotBlank
	@Size(max = 200)
	private String name;

	private String description;

	@NotNull
	private Long categoryId;

	private Integer ageLimitMin;

	private Integer ageLimitMax;

	@PositiveOrZero
	private BigDecimal membershipFee;
}
