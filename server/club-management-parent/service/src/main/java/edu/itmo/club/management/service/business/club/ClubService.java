package edu.itmo.club.management.service.business.club;

import edu.itmo.club.management.domain.entity.Club;
import edu.itmo.club.management.domain.enums.ClubStatus;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface ClubService {

	@NotNull
	List<Club> findAll();

	@NotNull
	Optional<Club> findById(@NotNull Long id);

	@NotNull
	List<Club> findByStatus(@NotNull ClubStatus status);

	@NotNull
	List<Club> findByCategoryId(@NotNull Long categoryId);

	@NotNull
	Club save(@NotNull Club club);

	void deleteById(@NotNull Long id);

	@NotNull
	List<Club> findByOwnerUserId(@NotNull Long userId);

	@NotNull
	List<Club> findClubsByMemberUserId(@NotNull Long userId);
}
