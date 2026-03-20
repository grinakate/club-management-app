package edu.itmo.club.management.app.mapper;

import edu.itmo.club.management.app.endpoint.dto.UserRegisterRequest;
import edu.itmo.club.management.app.endpoint.dto.UserUpdateRequest;
import edu.itmo.club.management.app.endpoint.dto.UserResponse;
import edu.itmo.club.management.domain.entity.User;
import edu.itmo.club.management.domain.enums.UserRole;
import edu.itmo.club.management.domain.enums.UserStatus;
import lombok.Setter;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Маппер для данных пользователя.
 */
@Mapper(componentModel = "spring")
public abstract class UserMapper {

	@Setter(onMethod_ = {@Autowired})
	protected PasswordEncoder passwordEncoder;

	/**
	 * Метод маппит сущность пользователя в ДТО.
	 *
	 * @param user сущность пользователя.
	 * @return ДТО.
	 */
	public abstract UserResponse mapToResponse(User user);

	/**
	 * Метод маппит список сущностей пользователей в ДТО.
	 *
	 * @param users список сущностей пользователя.
	 * @return список ДТО.
	 */
	public abstract List<UserResponse> mapToResponse(List<User> users);

	/**
	 * Метод маппит данные из запроса в сущность для сохранения в БД.
	 *
	 * @param source данные пользователя из запроса.
	 * @return сущность пользователя.
	 */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "role", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "password", expression = "java(passwordEncoder.encode(source.getPassword()))")
	@Mapping(target = "authorities", ignore = true)
	public abstract User mapForCreateParticipant(UserRegisterRequest source);

	@AfterMapping
	protected void afterMapping(@MappingTarget User target) {
		target.setStatus(UserStatus.ACTIVE);
		target.setRole(UserRole.PARTICIPANT);
		target.setCreatedAt(LocalDateTime.now());
	}

	/**
	 * Метод обновляет сущность данными из запроса.
	 *
	 * @param target обновляемая сущность.
	 * @param source данные пользователя из запроса.
	 */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "role", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	public abstract void mapForUpdate(@MappingTarget User target, UserUpdateRequest source);
}
