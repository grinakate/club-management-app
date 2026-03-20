package edu.itmo.club.management.domain.repository;

import edu.itmo.club.management.domain.entity.ClubMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubMembershipRepository extends JpaRepository<ClubMembership, Long> {

	List<ClubMembership> findByClubId(Long clubId);

	List<ClubMembership> findByUserId(Long userId);

	Optional<ClubMembership> findByClubIdAndUserId(Long clubId, Long userId);

	boolean existsByClubIdAndUserId(Long clubId, Long userId);
}
