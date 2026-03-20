package edu.itmo.club.management.service.business.membership;

import edu.itmo.club.management.domain.entity.Club;
import edu.itmo.club.management.domain.entity.MembershipApplication;
import edu.itmo.club.management.domain.entity.User;
import edu.itmo.club.management.domain.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface MembershipApplicationService {

	@NotNull
	MembershipApplication save(@NotNull MembershipApplication application);

	@NotNull
	Optional<MembershipApplication> findById(@NotNull Long id);

	@NotNull
	List<MembershipApplication> findByClubId(@NotNull Long clubId);

	@NotNull
	List<MembershipApplication> findByUserId(@NotNull Long userId);

	@NotNull
	List<MembershipApplication> findByClubIdAndUserId(@NotNull Long clubId, @NotNull Long userId);

	boolean hasActiveApplication(@NotNull Long clubId, @NotNull Long userId);

	@NotNull
	MembershipApplication apply(@NotNull Club club, @NotNull User user, String comment);

	@NotNull
	MembershipApplication review(@NotNull Long applicationId, @NotNull User reviewer, @NotNull ApplicationStatus decision, String comment);
}
