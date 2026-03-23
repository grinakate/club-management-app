package edu.itmo.club.management.app.endpoint.auth;

import edu.itmo.club.management.app.endpoint.dto.LoginRequest;
import edu.itmo.club.management.app.endpoint.dto.TokenResponse;
import edu.itmo.club.management.app.endpoint.dto.UserRegisterRequest;
import edu.itmo.club.management.app.endpoint.dto.UserResponse;
import edu.itmo.club.management.app.mapper.UserMapper;
import edu.itmo.club.management.app.security.JwtUtils;
import edu.itmo.club.management.domain.entity.User;
import edu.itmo.club.management.service.business.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final JwtUtils jwtUtils;
	private final UserMapper userMapper;
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	@PostMapping("/register")
	public UserResponse register(@Valid @RequestBody UserRegisterRequest request) {
		if (request.getEmail() == null && request.getPhone() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"У пользователя должен быть заполнен номер телефона или email");
		}

		if (request.getEmail() != null && !request.getEmail().isBlank()
				&& userService.existByEmail(request.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Этот Email уже занят");
		}

		if (request.getPhone() != null && !request.getPhone().isBlank()
				&& userService.existByPhone(request.getPhone())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Этот номер телефона уже занят");
		}

		User user = userMapper.mapForCreateParticipant(request);
		User savedUser = userService.save(user);

		return userMapper.mapToResponse(savedUser);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
		var userOpt = userService.findByLogin(request.getLogin());
		if (userOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
		}

		User user = userOpt.get();
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
		}

		String token = jwtUtils.generateToken(user);
		return ResponseEntity.ok(new TokenResponse(token));
	}

}
