package edu.itmo.club.management.domain.repository;

import edu.itmo.club.management.domain.entity.Club;
import edu.itmo.club.management.domain.enums.ClubStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClubRepository extends JpaRepository<Club, Long> {

	List<Club> findByStatus(ClubStatus status);

	@Query("SELECT c FROM Club c WHERE c.owner.id = :userId")
	List<Club> findByOwnerUserId(@Param("userId") Long userId);

	List<Club> findByCategoryId(Long categoryId);
}
