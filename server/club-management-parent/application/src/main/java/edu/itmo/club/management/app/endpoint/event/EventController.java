package edu.itmo.club.management.app.endpoint.event;

import edu.itmo.club.management.app.endpoint.dto.EventCreateRequest;
import edu.itmo.club.management.app.endpoint.dto.EventResponse;
import edu.itmo.club.management.app.endpoint.dto.EventUpdateRequest;
import edu.itmo.club.management.app.mapper.EventMapper;
import edu.itmo.club.management.domain.entity.Club;
import edu.itmo.club.management.domain.entity.Event;
import edu.itmo.club.management.domain.entity.User;
import edu.itmo.club.management.domain.enums.EventStatus;
import edu.itmo.club.management.service.business.club.ClubService;
import edu.itmo.club.management.service.business.event.EventService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
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

@Transactional
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class EventController {

	private final EventService eventService;
	private final EventMapper eventMapper;
	private final ClubService clubService;

	@GetMapping("/events")
	public List<EventResponse> findAll(@RequestParam(required = false) Long clubId,
									   @RequestParam(required = false) String status) {
		if (clubId != null) {
			return eventMapper.mapToResponse(eventService.findByClubId(clubId));
		}
		if (status != null) {
			try {
				EventStatus eventStatus = EventStatus.valueOf(status.toUpperCase());
				return eventMapper.mapToResponse(eventService.findByStatus(eventStatus));
			} catch (IllegalArgumentException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный статус: " + status);
			}
		}
		return eventMapper.mapToResponse(eventService.findAll());
	}

	@GetMapping("/events/{id}")
	public EventResponse findById(@PathVariable Long id) {
		Event event = eventService.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Мероприятие не найдено"));
		return eventMapper.mapToResponse(event);
	}

	@PostMapping("/events")
	public EventResponse create(@AuthenticationPrincipal User user,
								@Valid @RequestBody EventCreateRequest request) {
		Club club = clubService.findById(request.getClubId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Клуб не найден"));

		if (!club.getOwner().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Создавать мероприятия может только владелец клуба");
		}

		if (request.getStartAt() != null && request.getEndAt() != null
				&& !request.getEndAt().isAfter(request.getStartAt())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата окончания должна быть позже даты начала");
		}

		Event event = eventMapper.mapForCreate(request);
		event.setClub(club);
		event.setCreatedBy(user);
		event.setStatus(EventStatus.PUBLISHED);

		return eventMapper.mapToResponse(eventService.save(event));
	}

	@PutMapping("/events/{id}")
	public EventResponse update(@AuthenticationPrincipal User user,
								@PathVariable Long id,
								@Valid @RequestBody EventUpdateRequest request) {
		Event event = eventService.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Мероприятие не найдено"));

		if (!event.getCreatedBy().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Только создатель может редактировать мероприятие");
		}

		LocalDateTime effectiveStart = request.getStartAt() != null ? request.getStartAt() : event.getStartAt();
		LocalDateTime effectiveEnd = request.getEndAt() != null ? request.getEndAt() : event.getEndAt();
		if (!effectiveEnd.isAfter(effectiveStart)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата окончания должна быть позже даты начала");
		}

		eventMapper.mapForUpdate(event, request);
		return eventMapper.mapToResponse(eventService.save(event));
	}

	@DeleteMapping("/events/{id}")
	public ResponseEntity<Void> cancel(@AuthenticationPrincipal User user,
									   @PathVariable Long id) {
		Event event = eventService.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Мероприятие не найдено"));

		if (!event.getCreatedBy().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Только создатель может отменить мероприятие");
		}

		event.setStatus(EventStatus.CANCELLED);
		eventService.save(event);

		return ResponseEntity.noContent().build();
	}
}
