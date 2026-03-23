package edu.itmo.club.management.app.mapper;

import edu.itmo.club.management.app.endpoint.dto.EventCreateRequest;
import edu.itmo.club.management.app.endpoint.dto.EventUpdateRequest;
import edu.itmo.club.management.app.endpoint.dto.EventResponse;
import edu.itmo.club.management.domain.entity.Event;
import edu.itmo.club.management.domain.enums.EventStatus;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class EventMapper {

	@Mapping(source = "club.id", target = "clubId")
	@Mapping(source = "club.name", target = "clubName")
	@Mapping(source = "createdBy.id", target = "createdBy")
	@Mapping(source = "createdBy.fullName", target = "createdByFullName")
	public abstract EventResponse mapToResponse(Event event);

	public abstract List<EventResponse> mapToResponse(List<Event> events);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "club", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	public abstract Event mapForCreate(EventCreateRequest source);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "club", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "status", ignore = true)
	public abstract void mapForUpdate(@MappingTarget Event target, EventUpdateRequest source);

	@AfterMapping
	protected void afterUpdate(@MappingTarget Event target, EventUpdateRequest source) {
		if (source.getStatus() != null && !source.getStatus().isBlank()) {
			try {
				target.setStatus(EventStatus.valueOf(source.getStatus().toUpperCase()));
			} catch (IllegalArgumentException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный статус: " + source.getStatus());
			}
		}
	}
}
