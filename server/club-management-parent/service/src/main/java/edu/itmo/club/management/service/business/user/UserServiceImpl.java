package edu.itmo.club.management.service.business.user;

import edu.itmo.club.management.domain.entity.User;
import edu.itmo.club.management.domain.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для сущности {@link User}.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository repository;

	@NotNull
	@Override
	public List<User> findAll() {
		return repository.findAll();
	}

	@NotNull
	@Override
	public Optional<User> findById(@NotNull Long id) {
		return repository.findById(id);
	}

	@NotNull
	@Override
	public User save(@NotNull User user) {
		return repository.save(user);
	}
}
