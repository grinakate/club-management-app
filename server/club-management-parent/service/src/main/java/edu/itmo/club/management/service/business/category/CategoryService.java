package edu.itmo.club.management.service.business.category;

import edu.itmo.club.management.domain.entity.Category;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

	@NotNull
	List<Category> findAll();

	@NotNull
	Optional<Category> findById(@NotNull Long id);
}
