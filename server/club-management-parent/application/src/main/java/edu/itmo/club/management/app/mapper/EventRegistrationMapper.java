package edu.itmo.club.management.app.mapper;

import edu.itmo.club.management.app.endpoint.dto.RegistrationResponse;
import edu.itmo.club.management.domain.entity.EventRegistration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class EventRegistrationMapper {

	@Mapping(source = "event.id", target = "eventId")
	@Mapping(source = "event.title", target = "eventTitle")
	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "user.fullName", target = "userFullName")
	public abstract RegistrationResponse mapToResponse(EventRegistration registration);

	public abstract List<RegistrationResponse> mapToResponse(List<EventRegistration> list);
}
