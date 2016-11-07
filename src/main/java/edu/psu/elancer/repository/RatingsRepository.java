package edu.psu.elancer.repository;

import edu.psu.elancer.domain.Ratings;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Ratings entity.
 */
@SuppressWarnings("unused")
public interface RatingsRepository extends JpaRepository<Ratings,Long> {

}
