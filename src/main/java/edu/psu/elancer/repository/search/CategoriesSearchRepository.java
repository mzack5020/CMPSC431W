package edu.psu.elancer.repository.search;

import edu.psu.elancer.domain.Categories;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Categories entity.
 */
public interface CategoriesSearchRepository extends ElasticsearchRepository<Categories, Long> {
}
