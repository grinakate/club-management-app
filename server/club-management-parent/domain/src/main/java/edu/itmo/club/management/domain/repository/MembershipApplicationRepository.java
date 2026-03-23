package edu.itmo.club.management.domain.repository;

import edu.itmo.club.management.domain.entity.MembershipApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipApplicationRepository extends JpaRepository<MembershipApplication, Long> {

	List<MembershipApplication> findByClubId(Long clubId);

	List<MembershipApplication> findByUserId(Long userId);

	List<MembershipApplication> findByClubIdAndUserId(Long clubId, Long userId);
}
