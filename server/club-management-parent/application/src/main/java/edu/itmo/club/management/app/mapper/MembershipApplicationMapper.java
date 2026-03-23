package edu.itmo.club.management.app.mapper;

import edu.itmo.club.management.app.endpoint.dto.ApplicationResponse;
import edu.itmo.club.management.domain.entity.MembershipApplication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MembershipApplicationMapper {

	@Mapping(source = "club.id", target = "clubId")
	@Mapping(source = "club.name", target = "clubName")
	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "user.fullName", target = "userFullName")
	@Mapping(source = "reviewedBy.fullName", target = "reviewedByFullName")
	@Mapping(source = "status", target = "status")
	ApplicationResponse mapToResponse(MembershipApplication application);

	List<ApplicationResponse> mapToResponse(List<MembershipApplication> applications);
}
