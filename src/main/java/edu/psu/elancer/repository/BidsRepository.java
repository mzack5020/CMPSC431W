package edu.psu.elancer.repository;

import edu.psu.elancer.domain.Bids;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Bids entity.
 */
@SuppressWarnings("unused")
public interface BidsRepository extends JpaRepository<Bids,Long> {

}
