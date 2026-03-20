package edu.itmo.club.management.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "category")
public class Category {

	@Id
	@NonNull
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_sequence")
	@SequenceGenerator(sequenceName = "category_seq", name = "category_sequence", allocationSize = 1)
	private Long id;

	@NonNull
	@Column(name = "name", nullable = false)
	private String name;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Category category)) {
			return false;
		}
		return Objects.equals(getId(), category.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
