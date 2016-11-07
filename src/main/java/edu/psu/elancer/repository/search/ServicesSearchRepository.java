package edu.psu.elancer.repository.search;

import edu.psu.elancer.domain.Services;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Services entity.
 */
public interface ServicesSearchRepository extends ElasticsearchRepository<Services, Long> {
}
