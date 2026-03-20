package edu.itmo.club.management.domain.repository;

import edu.itmo.club.management.domain.entity.Event;
import edu.itmo.club.management.domain.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

	List<Event> findByClubId(Long clubId);

	List<Event> findByStatus(EventStatus status);
}
