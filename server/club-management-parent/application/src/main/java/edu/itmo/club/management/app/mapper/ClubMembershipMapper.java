package edu.itmo.club.management.app.mapper;

import edu.itmo.club.management.app.endpoint.dto.MembershipResponse;
import edu.itmo.club.management.domain.entity.ClubMembership;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClubMembershipMapper {

	@Mapping(source = "club.id", target = "clubId")
	@Mapping(source = "club.name", target = "clubName")
	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "user.fullName", target = "userFullName")
	@Mapping(source = "memberRole", target = "memberRole")
	@Mapping(source = "status", target = "status")
	MembershipResponse mapToResponse(ClubMembership membership);

	List<MembershipResponse> mapToResponse(List<ClubMembership> memberships);
}
