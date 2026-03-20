package edu.itmo.club.management.app.endpoint.event;

import edu.itmo.club.management.app.endpoint.dto.RegistrationResponse;
import edu.itmo.club.management.app.mapper.EventRegistrationMapper;
import edu.itmo.club.management.domain.entity.Event;
import edu.itmo.club.management.domain.entity.EventRegistration;
import edu.itmo.club.management.domain.entity.User;
import edu.itmo.club.management.domain.enums.EventStatus;
import edu.itmo.club.management.domain.enums.RegistrationStatus;
import edu.itmo.club.management.service.business.event.EventService;
import edu.itmo.club.management.service.business.registration.EventRegistrationService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class EventRegistrationController {

	private final EventRegistrationService registrationService;
	private final EventRegistrationMapper registrationMapper;
	private final EventService eventService;

	@PostMapping("/events/{eventId}/registrations")
	public RegistrationResponse register(@AuthenticationPrincipal User user,
										 @PathVariable Long eventId) {
		Event event = eventService.findById(eventId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Мероприятие не найдено"));

		if (event.getStatus() != EventStatus.PUBLISHED) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Регистрация доступна только для опубликованных мероприятий");
		}

		var existingOpt = registrationService.findByEventIdAndUserId(eventId, user.getId());
		if (existingOpt.isPresent()) {
			EventRegistration existing = existingOpt.get();
			if (existing.getStatus() != RegistrationStatus.CANCELLED) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Вы уже зарегистрированы на это мероприятие");
			}
			long count = registrationService.countRegistered(eventId);
			RegistrationStatus newStatus = (event.getParticipantLimit() == null || count < event.getParticipantLimit())
					? RegistrationStatus.REGISTERED
					: RegistrationStatus.WAITLIST;
			existing.setStatus(newStatus);
			existing.setRegisteredAt(LocalDateTime.now());
			return registrationMapper.mapToResponse(registrationService.save(existing));
		}

		long count = registrationService.countRegistered(eventId);
		RegistrationStatus status = (event.getParticipantLimit() == null || count < event.getParticipantLimit())
				? RegistrationStatus.REGISTERED
				: RegistrationStatus.WAITLIST;

		EventRegistration registration = new EventRegistration();
		registration.setEvent(event);
		registration.setUser(user);
		registration.setRegisteredAt(LocalDateTime.now());
		registration.setStatus(status);

		return registrationMapper.mapToResponse(registrationService.save(registration));
	}

	@DeleteMapping("/events/{eventId}/registrations")
	public ResponseEntity<Void> cancel(@AuthenticationPrincipal User user,
									   @PathVariable Long eventId) {
		EventRegistration registration = registrationService.findByEventIdAndUserId(eventId, user.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Регистрация не найдена"));

		registration.setStatus(RegistrationStatus.CANCELLED);
		registrationService.save(registration);

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/events/{eventId}/registrations")
	public List<RegistrationResponse> findByEvent(@AuthenticationPrincipal User user,
												  @PathVariable Long eventId) {
		Event event = eventService.findById(eventId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Мероприятие не найдено"));

		if (!event.getCreatedBy().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Только организатор может просматривать список участников");
		}

		return registrationMapper.mapToResponse(registrationService.findByEventId(eventId));
	}

	@PutMapping("/registrations/{id}/attendance")
	public RegistrationResponse markAttendance(@AuthenticationPrincipal User user,
											   @PathVariable Long id) {
		EventRegistration registration = registrationService.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Регистрация не найдена"));

		if (!registration.getEvent().getCreatedBy().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Только организатор может отмечать посещаемость");
		}

		registration.setAttendanceMark(true);
		return registrationMapper.mapToResponse(registrationService.save(registration));
	}

	@GetMapping("/users/me/registrations")
	public List<RegistrationResponse> myRegistrations(@AuthenticationPrincipal User user) {
		return registrationMapper.mapToResponse(registrationService.findByUserId(user.getId()));
	}
}
