package edu.psu.elancer.repository.search;

import edu.psu.elancer.domain.Bids;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Bids entity.
 */
public interface BidsSearchRepository extends ElasticsearchRepository<Bids, Long> {
}
