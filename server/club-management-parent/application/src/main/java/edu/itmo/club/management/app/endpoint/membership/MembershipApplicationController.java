package edu.itmo.club.management.app.endpoint.membership;

import edu.itmo.club.management.app.endpoint.dto.ApplicationCreateRequest;
import edu.itmo.club.management.app.endpoint.dto.ApplicationResponse;
import edu.itmo.club.management.app.endpoint.dto.ApplicationReviewRequest;
import edu.itmo.club.management.app.mapper.MembershipApplicationMapper;
import edu.itmo.club.management.domain.entity.Club;
import edu.itmo.club.management.domain.entity.User;
import edu.itmo.club.management.service.business.club.ClubService;
import edu.itmo.club.management.service.business.membership.MembershipApplicationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class MembershipApplicationController {

	private final MembershipApplicationService applicationService;
	private final ClubService clubService;
	private final MembershipApplicationMapper applicationMapper;

	@PostMapping("/clubs/{clubId}/applications")
	public ApplicationResponse apply(@AuthenticationPrincipal User user,
									 @PathVariable Long clubId,
									 @Valid @RequestBody(required = false) ApplicationCreateRequest request) {
		Club club = clubService.findById(clubId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Клуб не найден"));
		String comment = request != null ? request.getComment() : null;
		return applicationMapper.mapToResponse(applicationService.apply(club, user, comment));
	}

	@GetMapping("/clubs/{clubId}/applications")
	public List<ApplicationResponse> getByClub(@AuthenticationPrincipal User user,
												@PathVariable Long clubId) {
		Club club = clubService.findById(clubId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Клуб не найден"));
		if (!club.getOwner().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет доступа к заявкам этого клуба");
		}
		return applicationMapper.mapToResponse(applicationService.findByClubId(clubId));
	}

	@PutMapping("/applications/{id}/review")
	public ApplicationResponse review(@AuthenticationPrincipal User user,
									  @PathVariable Long id,
									  @Valid @RequestBody ApplicationReviewRequest request) {
		return applicationMapper.mapToResponse(
				applicationService.review(id, user, request.getDecision(), request.getComment())
		);
	}
}
