package edu.itmo.club.management.service.business.club;

import edu.itmo.club.management.domain.entity.Club;
import edu.itmo.club.management.domain.entity.ClubMembership;
import edu.itmo.club.management.domain.enums.ClubStatus;
import edu.itmo.club.management.domain.repository.ClubMembershipRepository;
import edu.itmo.club.management.domain.repository.ClubRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ClubServiceImpl implements ClubService {

	private final ClubRepository repository;
	private final ClubMembershipRepository membershipRepository;

	@NotNull
	@Override
	public List<Club> findAll() {
		return repository.findAll();
	}

	@NotNull
	@Override
	public Optional<Club> findById(@NotNull Long id) {
		return repository.findById(id);
	}

	@NotNull
	@Override
	public List<Club> findByStatus(@NotNull ClubStatus status) {
		return repository.findByStatus(status);
	}

	@NotNull
	@Override
	public List<Club> findByCategoryId(@NotNull Long categoryId) {
		return repository.findByCategoryId(categoryId);
	}

	@NotNull
	@Override
	public Club save(@NotNull Club club) {
		return repository.save(club);
	}

	@Override
	public void deleteById(@NotNull Long id) {
		repository.deleteById(id);
	}

	@NotNull
	@Override
	public List<Club> findByOwnerUserId(@NotNull Long userId) {
		return repository.findByOwnerUserId(userId);
	}

	@NotNull
	@Override
	public List<Club> findClubsByMemberUserId(@NotNull Long userId) {
		return membershipRepository.findByUserId(userId).stream()
				.map(ClubMembership::getClub)
				.toList();
	}
}
