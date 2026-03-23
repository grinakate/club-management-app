package edu.itmo.club.management.service.business.membership;

import edu.itmo.club.management.domain.entity.Club;
import edu.itmo.club.management.domain.entity.ClubMembership;
import edu.itmo.club.management.domain.entity.MembershipApplication;
import edu.itmo.club.management.domain.entity.User;
import edu.itmo.club.management.domain.enums.ApplicationStatus;
import edu.itmo.club.management.domain.enums.MembershipRole;
import edu.itmo.club.management.domain.enums.MembershipStatus;
import edu.itmo.club.management.domain.repository.MembershipApplicationRepository;
import edu.itmo.club.management.service.business.notification.NotificationService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MembershipApplicationServiceImpl implements MembershipApplicationService {

	private final MembershipApplicationRepository repository;
	private final ClubMembershipService membershipService;
	private final NotificationService notificationService;

	@NotNull
	@Override
	public MembershipApplication save(@NotNull MembershipApplication application) {
		return repository.save(application);
	}

	@NotNull
	@Override
	public Optional<MembershipApplication> findById(@NotNull Long id) {
		return repository.findById(id);
	}

	@NotNull
	@Override
	public List<MembershipApplication> findByClubId(@NotNull Long clubId) {
		return repository.findByClubId(clubId);
	}

	@NotNull
	@Override
	public List<MembershipApplication> findByUserId(@NotNull Long userId) {
		return repository.findByUserId(userId);
	}

	@NotNull
	@Override
	public List<MembershipApplication> findByClubIdAndUserId(@NotNull Long clubId, @NotNull Long userId) {
		return repository.findByClubIdAndUserId(clubId, userId);
	}

	@Override
	public boolean hasActiveApplication(@NotNull Long clubId, @NotNull Long userId) {
		return repository.findByClubIdAndUserId(clubId, userId).stream()
				.anyMatch(a -> a.getStatus() == ApplicationStatus.NEW);
	}

	@Transactional
	@Override
	public MembershipApplication apply(@NotNull Club club, @NotNull User user, String comment) {
		if (membershipService.existsByClubIdAndUserIdAndStatus(club.getId(), user.getId(), MembershipStatus.ACTIVE)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Вы уже состоите в этом клубе");
		}
		if (hasActiveApplication(club.getId(), user.getId())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Заявка уже подана и ожидает рассмотрения");
		}
		MembershipApplication application = new MembershipApplication();
		application.setClub(club);
		application.setUser(user);
		application.setAppliedAt(LocalDateTime.now());
		application.setStatus(ApplicationStatus.NEW);
		application.setComment(comment);
		return repository.save(application);
	}

	@Transactional
	@Override
	public MembershipApplication review(@NotNull Long applicationId, @NotNull User reviewer, @NotNull ApplicationStatus decision, String comment) {
		MembershipApplication application = repository.findById(applicationId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Заявка не найдена"));
		if (!application.getClub().getOwner().getId().equals(reviewer.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет прав для рассмотрения этой заявки");
		}
		if (application.getStatus() != ApplicationStatus.NEW) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Заявка уже рассмотрена");
		}
		if (decision != ApplicationStatus.APPROVED && decision != ApplicationStatus.REJECTED) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Решение должно быть APPROVED или REJECTED");
		}
		application.setStatus(decision);
		application.setReviewedBy(reviewer);
		application.setReviewedAt(LocalDateTime.now());
		if (comment != null) {
			application.setComment(comment);
		}
		if (decision == ApplicationStatus.APPROVED) {
			var clubMembershipOpt = membershipService.findByClubIdAndUserId(application.getClub().getId(), application.getUser().getId());
			if (clubMembershipOpt.isEmpty()) {
				ClubMembership membership = new ClubMembership();
				membership.setClub(application.getClub());
				membership.setUser(application.getUser());
				membership.setJoinedAt(LocalDateTime.now());
				membership.setMemberRole(MembershipRole.MEMBER);
				membership.setStatus(MembershipStatus.ACTIVE);
				membershipService.save(membership);
			} else {
				var clubMembership = clubMembershipOpt.get();
				if (clubMembership.getStatus().equals(MembershipStatus.ACTIVE)) {
					throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь уже является участником клуба");
				}
				clubMembership.setStatus(MembershipStatus.ACTIVE);
			}
		}
		MembershipApplication saved = repository.save(application);
		notificationService.notifyApplicantOnApplicationReviewed(saved);
		return saved;
	}
}
