package edu.itmo.club.management.app.endpoint.user;

import edu.itmo.club.management.app.endpoint.user.dto.CreateUserRequest;
import edu.itmo.club.management.app.endpoint.user.dto.CreateUserResponse;
import edu.itmo.club.management.app.endpoint.user.dto.UpdateUserRequest;
import edu.itmo.club.management.app.endpoint.user.dto.UserDto;
import edu.itmo.club.management.app.mapper.UserMapper;
import edu.itmo.club.management.domain.entity.User;
import edu.itmo.club.management.service.business.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Контроллер для управления пользователями.
 */
@Validated
@Transactional
@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService service;
	private final UserMapper mapper;

	/**
	 * Метод получения всех пользователей в приложении.
	 *
	 * @return список пользователей.
	 */
	@GetMapping
	public List<UserDto> findAll() {
		return mapper.mapToDto(service.findAll());
	}

	/**
	 * Метод получения пользователя по ИД.
	 *
	 * @param id ИД пользователя.
	 * @return Данные пользователя.
	 */
	@GetMapping(value = "/{id}")
	public UserDto findById(@NotNull @PathVariable("id") Long id) {
		Optional<User> user = service.findById(id);
		if (user.isPresent()) {
			return mapper.mapToDto(user.get());
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		}
	}

	/**
	 * Метод создания пользователя.
	 *
	 * @param request Данные нового пользователя.
	 * @return ИД созданного пользователя.
	 */
	@PostMapping
	public CreateUserResponse create(@Valid @RequestBody CreateUserRequest request) {
		User user = mapper.mapToEntity(request);
		return new CreateUserResponse(service.save(user).getId());
	}

	/**
	 * Метод обновления данных пользователя.
	 *
	 * @param id      ИД обновляемого пользователя
	 * @param request Новые данные пользователя.
	 * @return Обновленные данные пользователя.
	 */
	@PutMapping("/{id}")
	public UserDto put(@NotNull @PathVariable("id") Long id,
					   @Valid @RequestBody UpdateUserRequest request) {
		Optional<User> userOpt = service.findById(id);
		if (userOpt.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		}
		User user = userOpt.get();
		mapper.mapForUpdate(user, request);
		User updatedUser = service.save(user);

		return mapper.mapToDto(updatedUser);
	}
}