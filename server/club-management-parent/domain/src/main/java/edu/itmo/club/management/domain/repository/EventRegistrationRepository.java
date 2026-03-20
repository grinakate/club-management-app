package edu.itmo.club.management.domain.repository;

import edu.itmo.club.management.domain.entity.EventRegistration;
import edu.itmo.club.management.domain.enums.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

	List<EventRegistration> findByEventId(Long eventId);

	List<EventRegistration> findByUserId(Long userId);

	Optional<EventRegistration> findByEventIdAndUserId(Long eventId, Long userId);

	long countByEventIdAndStatus(Long eventId, RegistrationStatus status);
}
