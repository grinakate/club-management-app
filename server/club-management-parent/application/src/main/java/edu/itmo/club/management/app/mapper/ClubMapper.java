package edu.itmo.club.management.app.mapper;

import edu.itmo.club.management.app.endpoint.dto.CategoryResponse;
import edu.itmo.club.management.app.endpoint.dto.ClubCreateRequest;
import edu.itmo.club.management.app.endpoint.dto.ClubResponse;
import edu.itmo.club.management.app.endpoint.dto.ClubUpdateRequest;
import edu.itmo.club.management.domain.entity.Category;
import edu.itmo.club.management.domain.entity.Club;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ClubMapper {

	@Mapping(source = "category.id", target = "categoryId")
	@Mapping(source = "category.name", target = "categoryName")
	@Mapping(source = "owner.id", target = "ownerUserId")
	@Mapping(source = "owner.fullName", target = "ownerFullName")
	public abstract ClubResponse mapToResponse(Club club);

	public abstract List<ClubResponse> mapToResponse(List<Club> clubs);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "owner", ignore = true)
	@Mapping(target = "category", ignore = true)
	public abstract Club mapForCreate(ClubCreateRequest source);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "owner", ignore = true)
	@Mapping(target = "category", ignore = true)
	public abstract void mapForUpdate(@MappingTarget Club target, ClubUpdateRequest source);

	public abstract CategoryResponse mapToResponse(Category category);

	public abstract List<CategoryResponse> mapToCategoryResponse(List<Category> categories);
}
