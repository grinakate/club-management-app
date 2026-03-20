package edu.itmo.club.management.service.business.registration;

import edu.itmo.club.management.domain.entity.EventRegistration;
import edu.itmo.club.management.domain.enums.RegistrationStatus;
import edu.itmo.club.management.domain.repository.EventRegistrationRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EventRegistrationServiceImpl implements EventRegistrationService {

	private final EventRegistrationRepository repository;

	@NotNull
	@Override
	public Optional<EventRegistration> findById(@NotNull Long id) {
		return repository.findById(id);
	}

	@NotNull
	@Override
	public List<EventRegistration> findByEventId(@NotNull Long eventId) {
		return repository.findByEventId(eventId);
	}

	@NotNull
	@Override
	public List<EventRegistration> findByUserId(@NotNull Long userId) {
		return repository.findByUserId(userId);
	}

	@NotNull
	@Override
	public Optional<EventRegistration> findByEventIdAndUserId(@NotNull Long eventId, @NotNull Long userId) {
		return repository.findByEventIdAndUserId(eventId, userId);
	}

	@NotNull
	@Override
	public EventRegistration save(@NotNull EventRegistration registration) {
		return repository.save(registration);
	}

	@Override
	public long countRegistered(@NotNull Long eventId) {
		return repository.countByEventIdAndStatus(eventId, RegistrationStatus.REGISTERED);
	}
}
