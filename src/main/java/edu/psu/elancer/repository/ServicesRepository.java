package edu.psu.elancer.repository;

import edu.psu.elancer.domain.Services;
import edu.psu.elancer.domain.Categories;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Services entity.
 */
@SuppressWarnings("unused")
public interface ServicesRepository extends JpaRepository<Services,Long> {
    List<Services> findByCustomerId(Long id);
    List<Services> findByCategories(Categories category);
}
