package edu.psu.elancer.repository.search;

import edu.psu.elancer.domain.Contractor;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Contractor entity.
 */
public interface ContractorSearchRepository extends ElasticsearchRepository<Contractor, Long> {
}
