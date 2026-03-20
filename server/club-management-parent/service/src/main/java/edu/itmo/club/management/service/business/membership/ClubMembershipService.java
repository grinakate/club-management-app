package edu.itmo.club.management.service.business.membership;

import edu.itmo.club.management.domain.entity.Club;
import edu.itmo.club.management.domain.entity.ClubMembership;
import edu.itmo.club.management.domain.entity.User;
import edu.itmo.club.management.domain.enums.MembershipRole;
import edu.itmo.club.management.domain.enums.MembershipStatus;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface ClubMembershipService {

	@NotNull
	ClubMembership save(@NotNull ClubMembership membership);

	@NotNull
	Optional<ClubMembership> findById(@NotNull Long id);

	@NotNull
	List<ClubMembership> findByClubId(@NotNull Long clubId);

	@NotNull
	List<ClubMembership> findByUserId(@NotNull Long userId);

	@NotNull
	Optional<ClubMembership> findByClubIdAndUserId(@NotNull Long clubId, @NotNull Long userId);

	boolean existsByClubIdAndUserId(@NotNull Long clubId, @NotNull Long userId);

	boolean existsByClubIdAndUserIdAndStatus(@NotNull Long clubId, @NotNull Long userId, @NotNull MembershipStatus status);

	@NotNull
	List<ClubMembership> findByClubIdAndStatus(@NotNull Long clubId, @NotNull MembershipStatus status);

	void deleteById(@NotNull Long id);

	@NotNull
	ClubMembership updateRole(@NotNull Long membershipId, @NotNull Long requestingUserId, @NotNull MembershipRole role);

	void removeMember(@NotNull Long membershipId, @NotNull Long requestingUserId);

	void leaveClub(@NotNull Club club, @NotNull User user);
}
