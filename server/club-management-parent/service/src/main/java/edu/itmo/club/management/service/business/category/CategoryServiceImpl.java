package edu.itmo.club.management.service.business.category;

import edu.itmo.club.management.domain.entity.Category;
import edu.itmo.club.management.domain.repository.CategoryRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository repository;

	@NotNull
	@Override
	public List<Category> findAll() {
		return repository.findAll();
	}

	@NotNull
	@Override
	public Optional<Category> findById(@NotNull Long id) {
		return repository.findById(id);
	}
}
