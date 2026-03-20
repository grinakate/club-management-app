package edu.itmo.club.management.service.business.event;

import edu.itmo.club.management.domain.entity.Club;
import edu.itmo.club.management.domain.entity.Event;
import edu.itmo.club.management.domain.entity.User;
import edu.itmo.club.management.domain.enums.EventStatus;
import edu.itmo.club.management.domain.repository.EventRepository;
import edu.itmo.club.management.service.business.notification.NotificationService;
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
public class EventServiceImpl implements EventService {

	private final EventRepository repository;
	private final NotificationService notificationService;

	@NotNull
	@Override
	public List<Event> findAll() {
		return repository.findAll();
	}

	@NotNull
	@Override
	public Optional<Event> findById(@NotNull Long id) {
		return repository.findById(id);
	}

	@NotNull
	@Override
	public List<Event> findByClubId(@NotNull Long clubId) {
		return repository.findByClubId(clubId);
	}

	@NotNull
	@Override
	public List<Event> findByStatus(@NotNull EventStatus status) {
		return repository.findByStatus(status);
	}

	@NotNull
	@Override
	public Event save(@NotNull Event event) {
		return repository.save(event);
	}

	@Override
	public void deleteById(@NotNull Long id) {
		repository.deleteById(id);
	}

	@Transactional
	@NotNull
	@Override
	public Event createEvent(@NotNull Club club, @NotNull User createdBy, @NotNull Event event) {
		if (!club.getOwner().getId().equals(createdBy.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Создавать мероприятия может только владелец клуба");
		}
		event.setClub(club);
		event.setCreatedBy(createdBy);
		event.setStatus(EventStatus.PUBLISHED);
		Event saved = repository.save(event);
		notificationService.notifyClubMembersOnEventCreated(saved);
		return saved;
	}

	@Transactional
	@NotNull
	@Override
	public Event cancelEvent(@NotNull Long eventId, @NotNull Long requestingUserId) {
		Event event = repository.findById(eventId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Мероприятие не найдено"));
		if (!event.getCreatedBy().getId().equals(requestingUserId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Только создатель может отменить мероприятие");
		}
		event.setStatus(EventStatus.CANCELLED);
		Event saved = repository.save(event);
		notificationService.notifyRegistrantsOnEventCancelled(saved);
		return saved;
	}
}
