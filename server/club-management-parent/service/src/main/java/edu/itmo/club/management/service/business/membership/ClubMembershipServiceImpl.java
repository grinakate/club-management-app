package edu.itmo.club.management.service.business.membership;

import edu.itmo.club.management.domain.entity.Club;
import edu.itmo.club.management.domain.entity.ClubMembership;
import edu.itmo.club.management.domain.entity.User;
import edu.itmo.club.management.domain.enums.MembershipRole;
import edu.itmo.club.management.domain.enums.MembershipStatus;
import edu.itmo.club.management.domain.repository.ClubMembershipRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ClubMembershipServiceImpl implements ClubMembershipService {

	private final ClubMembershipRepository repository;

	@NotNull
	@Override
	public ClubMembership save(@NotNull ClubMembership membership) {
		return repository.save(membership);
	}

	@NotNull
	@Override
	public Optional<ClubMembership> findById(@NotNull Long id) {
		return repository.findById(id);
	}

	@NotNull
	@Override
	public List<ClubMembership> findByClubId(@NotNull Long clubId) {
		return repository.findByClubId(clubId);
	}

	@NotNull
	@Override
	public List<ClubMembership> findByUserId(@NotNull Long userId) {
		return repository.findByUserId(userId);
	}

	@NotNull
	@Override
	public Optional<ClubMembership> findByClubIdAndUserId(@NotNull Long clubId, @NotNull Long userId) {
		return repository.findByClubIdAndUserId(clubId, userId);
	}

	@Override
	public boolean existsByClubIdAndUserId(@NotNull Long clubId, @NotNull Long userId) {
		return repository.existsByClubIdAndUserId(clubId, userId);
	}

	@Override
	public boolean existsByClubIdAndUserIdAndStatus(@NotNull Long clubId, @NotNull Long userId, @NotNull MembershipStatus status) {
		return repository.existsByClubIdAndUserIdAndStatus(clubId, userId, status);
	}

	@NotNull
	@Override
	public List<ClubMembership> findByClubIdAndStatus(@NotNull Long clubId, @NotNull MembershipStatus status) {
		return repository.findByClubIdAndStatus(clubId, status);
	}

	@Override
	public void deleteById(@NotNull Long id) {
		repository.deleteById(id);
	}

	@Transactional
	@Override
	public ClubMembership updateRole(@NotNull Long membershipId, @NotNull Long requestingUserId, @NotNull MembershipRole role) {
		ClubMembership membership = repository.findById(membershipId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Членство не найдено"));
		if (!membership.getClub().getOwner().getId().equals(requestingUserId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет прав для изменения роли участника");
		}
		membership.setMemberRole(role);
		return repository.save(membership);
	}

	@Transactional
	@Override
	public void removeMember(@NotNull Long membershipId, @NotNull Long requestingUserId) {
		ClubMembership membership = repository.findById(membershipId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Членство не найдено"));
		if (!membership.getClub().getOwner().getId().equals(requestingUserId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет прав для исключения участника");
		}
		if (membership.getUser().getId().equals(membership.getClub().getOwner().getId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя исключить владельца клуба");
		}
		repository.deleteById(membershipId);
	}

	@Transactional
	@Override
	public void leaveClub(@NotNull Club club, @NotNull User user) {
		if (club.getOwner().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Владелец не может покинуть свой клуб");
		}
		ClubMembership membership = repository.findByClubIdAndUserId(club.getId(), user.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Вы не состоите в этом клубе"));
		membership.setStatus(MembershipStatus.LEFT);
		repository.save(membership);
	}
}
