package edu.itmo.club.management.service.business.user;

import edu.itmo.club.management.domain.entity.User;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для сущности {@link User}.
 */
public interface UserService {

	/**
	 * Метод получения всех пользователей.
	 *
	 * @return список пользователей.
	 */
	@NotNull
	List<User> findAll();

	/**
	 * Метод получения пользователя по ИД.
	 *
	 * @param id ИД пользователя.
	 * @return пользователь.
	 */
	@NotNull
	Optional<User> findById(@NotNull Long id);

	/**
	 * Сохраняет нового пользователя в БД.
	 *
	 * @param user пользователь.
	 * @return Сущность пользователя.
	 */
	@NotNull
	User save(@NotNull User user);
}
