package edu.itmo.club.management.app.endpoint.membership;

import edu.itmo.club.management.app.endpoint.dto.MemberRoleUpdateRequest;
import edu.itmo.club.management.app.endpoint.dto.MembershipResponse;
import edu.itmo.club.management.app.mapper.ClubMembershipMapper;
import edu.itmo.club.management.domain.entity.Club;
import edu.itmo.club.management.domain.entity.User;
import edu.itmo.club.management.domain.enums.MembershipStatus;
import edu.itmo.club.management.service.business.club.ClubService;
import edu.itmo.club.management.service.business.membership.ClubMembershipService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class ClubMembershipController {

	private final ClubMembershipService membershipService;
	private final ClubService clubService;
	private final ClubMembershipMapper membershipMapper;

	@GetMapping("/clubs/{clubId}/members")
	public List<MembershipResponse> getMembers(@AuthenticationPrincipal User user,
											   @PathVariable Long clubId) {
		Club club = clubService.findById(clubId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Клуб не найден"));
		boolean isOwner = club.getOwner().getId().equals(user.getId());
		boolean isMember = membershipService.existsByClubIdAndUserIdAndStatus(clubId, user.getId(), MembershipStatus.ACTIVE);
		if (!isOwner && !isMember) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет доступа к списку участников");
		}
		return membershipMapper.mapToResponse(membershipService.findByClubIdAndStatus(clubId, MembershipStatus.ACTIVE));
	}

	@PutMapping("/memberships/{id}/role")
	public MembershipResponse updateRole(@AuthenticationPrincipal User user,
										 @PathVariable Long id,
										 @Valid @RequestBody MemberRoleUpdateRequest request) {
		return membershipMapper.mapToResponse(membershipService.updateRole(id, user.getId(), request.getRole()));
	}

	@DeleteMapping("/memberships/{id}")
	public ResponseEntity<Void> removeMember(@AuthenticationPrincipal User user,
											 @PathVariable Long id) {
		membershipService.removeMember(id, user.getId());
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/clubs/{clubId}/leave")
	public ResponseEntity<Void> leaveClub(@AuthenticationPrincipal User user,
										  @PathVariable Long clubId) {
		Club club = clubService.findById(clubId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Клуб не найден"));
		membershipService.leaveClub(club, user);
		return ResponseEntity.noContent().build();
	}
}
