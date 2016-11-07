package edu.psu.elancer.repository;

import edu.psu.elancer.domain.Contractor;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Contractor entity.
 */
@SuppressWarnings("unused")
public interface ContractorRepository extends JpaRepository<Contractor,Long> {

}
