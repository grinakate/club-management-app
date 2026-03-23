package edu.itmo.club.management.domain.repository;

import edu.itmo.club.management.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
