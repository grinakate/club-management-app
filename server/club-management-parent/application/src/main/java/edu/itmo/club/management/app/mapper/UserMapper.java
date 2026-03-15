package edu.itmo.club.management.app.mapper;

import edu.itmo.club.management.app.endpoint.user.dto.CreateUserRequest;
import edu.itmo.club.management.app.endpoint.user.dto.UpdateUserRequest;
import edu.itmo.club.management.app.endpoint.user.dto.UserDto;
import edu.itmo.club.management.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Маппер для данных пользователя.
 */
@Mapper(componentModel = "spring")
public abstract class UserMapper {

	/**
	 * Метод маппит сущность пользователя в ДТО.
	 *
	 * @param user сущность пользователя.
	 * @return ДТО.
	 */
	public abstract UserDto mapToDto(User user);

	/**
	 * Метод маппит список сущностей пользователей в ДТО.
	 *
	 * @param users список сущностей пользователя.
	 * @return список ДТО.
	 */
	public abstract List<UserDto> mapToDto(List<User> users);

	/**
	 * Метод маппит данные из запроса в сущность для сохранения в БД.
	 *
	 * @param source данные пользователя из запроса.
	 * @return сущность пользователя.
	 */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "password", expression = "java(source.getPassword())") // TODO: encode(source.getPassword())
	public abstract User mapToEntity(CreateUserRequest source);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "password", ignore = true)
	public abstract void mapForUpdate(@MappingTarget User target, UpdateUserRequest source);
}
