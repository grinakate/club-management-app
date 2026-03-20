package edu.itmo.club.management.service.business.event;

import edu.itmo.club.management.domain.entity.Event;
import edu.itmo.club.management.domain.enums.EventStatus;
import edu.itmo.club.management.domain.repository.EventRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

	private final EventRepository repository;

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
}
