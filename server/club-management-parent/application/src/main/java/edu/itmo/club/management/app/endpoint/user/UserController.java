package edu.itmo.club.management.app.endpoint.user;

import edu.itmo.club.management.app.endpoint.dto.UserResponse;
import edu.itmo.club.management.app.endpoint.dto.UserUpdateRequest;
import edu.itmo.club.management.app.mapper.UserMapper;
import edu.itmo.club.management.domain.entity.User;
import edu.itmo.club.management.service.business.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/api/v1")
public class UserController {

	private final UserService service;
	private final UserMapper mapper;
	private final UserService userService;

	/**
	 * Метод получения всех пользователей в приложении.
	 *
	 * @return список пользователей.
	 */
	@GetMapping("/users")
	public List<UserResponse> findAll() {
		return mapper.mapToResponse(service.findAll());
	}

	/**
	 * Метод получения пользователя по ИД.
	 *
	 * @param id ИД пользователя.
	 * @return Данные пользователя.
	 */
	@GetMapping(value = "/users/{id}")
	public UserResponse findById(@NotNull @PathVariable("id") Long id) {
		Optional<User> user = service.findById(id);
		if (user.isPresent()) {
			return mapper.mapToResponse(user.get());
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
		}
	}

	/**
	 * Метод обновления данных пользователя.
	 *
	 * @param id      ИД обновляемого пользователя
	 * @param request Новые данные пользователя.
	 * @return Обновленные данные пользователя.
	 */
	@PutMapping("/users/{id}/profile")
	@PreAuthorize("#id == authentication.principal.id")
	public ResponseEntity<?> updateProfile(@NotNull @PathVariable("id") Long id,
										   @Valid @RequestBody UserUpdateRequest request) {
		var userOpt = userService.findById(id);
		if (userOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
		}

		User user = userOpt.get();
		if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
			if (userService.existByEmail(request.getEmail())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Этот Email уже занят");
			}
		}

		if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
			if (userService.existByEmail(request.getPhone())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Этот Номер телефона уже занят");
			}
		}

		mapper.mapForUpdate(user, request);
		service.save(user);

		return ResponseEntity.status(HttpStatus.OK).body("Профиль успешно обновлен");
	}
}