package edu.itmo.club.management.service.business.user;

import edu.itmo.club.management.domain.entity.User;
import edu.itmo.club.management.domain.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

	@NotNull
	@Override
	public Optional<User> findByLogin(@NotNull String login) {
		return repository.findByLogin(login);
	}

	@Override
	public boolean existByEmail(String email) {
		return repository.existsByEmail(email);
	}

	@Override
	public boolean existByPhone(String phone) {
		return repository.existsByPhone(phone);
	}

	@Override
	public User loadUserByUsername(String id) throws UsernameNotFoundException {
		try {
			return repository.findById(Long.parseLong(id))
					.orElseThrow(() -> new UsernameNotFoundException("Пользователь с id=" + id + " не найден"));
		} catch (NumberFormatException e) {
			throw new UsernameNotFoundException("Некорректный идентификатор пользователя: " + id, e);
		}
	}
}
