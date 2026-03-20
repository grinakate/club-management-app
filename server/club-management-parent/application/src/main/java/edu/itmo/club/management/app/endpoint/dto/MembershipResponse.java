package edu.itmo.club.management.app.endpoint.dto;

import edu.itmo.club.management.domain.enums.MembershipRole;
import edu.itmo.club.management.domain.enums.MembershipStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MembershipResponse {

	private Long id;
	private Long clubId;
	private String clubName;
	private Long userId;
	private String userFullName;
	private LocalDateTime joinedAt;
	private MembershipRole memberRole;
	private MembershipStatus status;
}
