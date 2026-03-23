package edu.itmo.club.management.service.business.registration;

import edu.itmo.club.management.domain.entity.EventRegistration;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface EventRegistrationService {

	@NotNull
	Optional<EventRegistration> findById(@NotNull Long id);

	@NotNull
	List<EventRegistration> findByEventId(@NotNull Long eventId);

	@NotNull
	List<EventRegistration> findByUserId(@NotNull Long userId);

	@NotNull
	Optional<EventRegistration> findByEventIdAndUserId(@NotNull Long eventId, @NotNull Long userId);

	@NotNull
	EventRegistration save(@NotNull EventRegistration registration);

	long countRegistered(@NotNull Long eventId);
}
