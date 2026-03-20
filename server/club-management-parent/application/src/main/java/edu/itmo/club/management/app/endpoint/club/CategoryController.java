package edu.itmo.club.management.app.endpoint.club;

import edu.itmo.club.management.app.endpoint.dto.CategoryResponse;
import edu.itmo.club.management.app.mapper.ClubMapper;
import edu.itmo.club.management.service.business.category.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class CategoryController {

	private final CategoryService categoryService;
	private final ClubMapper clubMapper;

	@GetMapping("/categories")
	public List<CategoryResponse> findAll() {
		return clubMapper.mapToCategoryResponse(categoryService.findAll());
	}
}
