package edu.itmo.club.management.domain.repository;

import edu.itmo.club.management.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для сущности {@link User}.
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
