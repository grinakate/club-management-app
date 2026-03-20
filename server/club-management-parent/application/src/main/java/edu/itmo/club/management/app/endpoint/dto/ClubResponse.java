package edu.itmo.club.management.app.endpoint.dto;

import edu.itmo.club.management.domain.enums.ClubStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ClubResponse {

	private Long id;

	private String name;

	private String description;

	private Long categoryId;

	private String categoryName;

	private Integer ageLimitMin;

	private Integer ageLimitMax;

	private BigDecimal membershipFee;

	private Long ownerUserId;

	private String ownerFullName;

	private ClubStatus status;

	private LocalDateTime createdAt;
}
