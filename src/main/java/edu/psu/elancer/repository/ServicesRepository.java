package edu.psu.elancer.repository;

import edu.psu.elancer.domain.Services;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Services entity.
 */
@SuppressWarnings("unused")
public interface ServicesRepository extends JpaRepository<Services,Long> {
    List<Services> findByCustomerId(Long id);
}
