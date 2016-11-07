package edu.psu.elancer.repository;

import edu.psu.elancer.domain.Categories;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Categories entity.
 */
@SuppressWarnings("unused")
public interface CategoriesRepository extends JpaRepository<Categories,Long> {

}
