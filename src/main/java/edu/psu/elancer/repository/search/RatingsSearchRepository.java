package edu.psu.elancer.repository.search;

import edu.psu.elancer.domain.Ratings;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Ratings entity.
 */
public interface RatingsSearchRepository extends ElasticsearchRepository<Ratings, Long> {
}
