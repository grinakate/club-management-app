package edu.itmo.club.management.app.endpoint.dto;

import edu.itmo.club.management.domain.enums.MembershipRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRoleUpdateRequest {

	@NotNull
	private MembershipRole role;
}
