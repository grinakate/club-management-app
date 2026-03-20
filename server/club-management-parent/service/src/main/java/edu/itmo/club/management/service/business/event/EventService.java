package edu.itmo.club.management.service.business.event;

import edu.itmo.club.management.domain.entity.Event;
import edu.itmo.club.management.domain.enums.EventStatus;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface EventService {

	@NotNull
	List<Event> findAll();

	@NotNull
	Optional<Event> findById(@NotNull Long id);

	@NotNull
	List<Event> findByClubId(@NotNull Long clubId);

	@NotNull
	List<Event> findByStatus(@NotNull EventStatus status);

	@NotNull
	Event save(@NotNull Event event);

	void deleteById(@NotNull Long id);
}
