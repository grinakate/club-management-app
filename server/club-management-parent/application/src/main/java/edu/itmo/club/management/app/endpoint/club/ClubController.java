package edu.itmo.club.management.app.endpoint.club;

import edu.itmo.club.management.app.endpoint.dto.ClubCreateRequest;
import edu.itmo.club.management.app.endpoint.dto.ClubResponse;
import edu.itmo.club.management.app.endpoint.dto.ClubUpdateRequest;
import edu.itmo.club.management.app.endpoint.dto.EventResponse;
import edu.itmo.club.management.app.mapper.ClubMapper;
import edu.itmo.club.management.app.mapper.EventMapper;
import edu.itmo.club.management.domain.entity.Category;
import edu.itmo.club.management.domain.entity.Club;
import edu.itmo.club.management.domain.entity.User;
import edu.itmo.club.management.domain.enums.ClubStatus;
import edu.itmo.club.management.service.business.category.CategoryService;
import edu.itmo.club.management.service.business.club.ClubService;
import edu.itmo.club.management.service.business.event.EventService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@Transactional
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class ClubController {

	private final ClubService clubService;
	private final ClubMapper clubMapper;
	private final CategoryService categoryService;
	private final EventService eventService;
	private final EventMapper eventMapper;

	@GetMapping("/clubs")
	public List<ClubResponse> findAll(@RequestParam(required = false) Long categoryId,
									  @RequestParam(required = false) ClubStatus status) {
		if (categoryId != null) {
			return clubMapper.mapToResponse(clubService.findByCategoryId(categoryId));
		}
		if (status != null) {
			return clubMapper.mapToResponse(clubService.findByStatus(status));
		}
		return clubMapper.mapToResponse(clubService.findAll());
	}

	@GetMapping("/clubs/{id}")
	public ClubResponse findById(@PathVariable("id") Long id) {
		Club club = clubService.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Клуб не найден"));
		return clubMapper.mapToResponse(club);
	}

	@PostMapping("/clubs")
	public ClubResponse create(@AuthenticationPrincipal User user,
							   @Valid @RequestBody ClubCreateRequest request) {
		if (request.getAgeLimitMin() != null && request.getAgeLimitMax() != null
				&& request.getAgeLimitMin() > request.getAgeLimitMax()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Минимальный возраст не может быть больше максимального");
		}

		Category category = categoryService.findById(request.getCategoryId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Категория не найдена"));

		Club club = clubMapper.mapForCreate(request);
		club.setOwner(user);
		club.setCategory(category);
		club.setStatus(ClubStatus.ACTIVE);
		club.setCreatedAt(LocalDateTime.now());

		return clubMapper.mapToResponse(clubService.save(club));
	}

	@PutMapping("/clubs/{id}")
	@PreAuthorize("@clubService.findById(#id).isPresent() && @clubService.findById(#id).get().owner.id == authentication.principal.id")
	public ClubResponse update(@PathVariable("id") Long id,
							   @Valid @RequestBody ClubUpdateRequest request) {
		if (request.getAgeLimitMin() != null && request.getAgeLimitMax() != null
				&& request.getAgeLimitMin() > request.getAgeLimitMax()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Минимальный возраст не может быть больше максимального");
		}

		Club club = clubService.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Клуб не найден"));

		clubMapper.mapForUpdate(club, request);

		if (request.getCategoryId() != null) {
			Category category = categoryService.findById(request.getCategoryId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Категория не найдена"));
			club.setCategory(category);
		}

		return clubMapper.mapToResponse(clubService.save(club));
	}

	@DeleteMapping("/clubs/{id}")
	@PreAuthorize("@clubService.findById(#id).isPresent() && @clubService.findById(#id).get().owner.id == authentication.principal.id")
	public ResponseEntity<Void> archive(@PathVariable("id") Long id) {
		Club club = clubService.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Клуб не найден"));

		club.setStatus(ClubStatus.ARCHIVED);
		clubService.save(club);

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/clubs/{clubId}/events")
	public List<EventResponse> getClubEvents(@PathVariable("clubId") Long clubId) {
		clubService.findById(clubId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Клуб не найден"));
		return eventMapper.mapToResponse(eventService.findByClubId(clubId));
	}

	@GetMapping("/users/me/clubs")
	public List<ClubResponse> getMyClubs(@AuthenticationPrincipal User user) {
		return clubMapper.mapToResponse(clubService.findClubsByMemberUserId(user.getId()));
	}
}
