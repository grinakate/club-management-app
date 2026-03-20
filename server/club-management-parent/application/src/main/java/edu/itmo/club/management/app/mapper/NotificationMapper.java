package edu.itmo.club.management.app.mapper;

import edu.itmo.club.management.app.endpoint.dto.NotificationResponse;
import edu.itmo.club.management.domain.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "event.id", target = "eventId")
	@Mapping(source = "club.id", target = "clubId")
	NotificationResponse mapToResponse(Notification notification);

	List<NotificationResponse> mapToResponse(List<Notification> notifications);
}
