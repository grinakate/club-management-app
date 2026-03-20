package edu.itmo.club.management.domain.repository;

import edu.itmo.club.management.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Репозиторий для сущности {@link User}.
 */
public interface UserRepository extends JpaRepository<User, Long> {

	@Query("SELECT u FROM User u WHERE u.email = :login OR u.phone = :login")
	Optional<User> findByLogin(@Param("login") String login);

	boolean existsByEmail(@Param("email") String email);

	boolean existsByPhone(@Param("phone") String phone);
}
