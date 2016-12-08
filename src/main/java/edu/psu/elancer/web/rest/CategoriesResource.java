package edu.psu.elancer.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.psu.elancer.domain.Categories;

import edu.psu.elancer.repository.CategoriesRepository;
import edu.psu.elancer.repository.search.CategoriesSearchRepository;
import edu.psu.elancer.web.rest.util.HeaderUtil;
import edu.psu.elancer.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Categories.
 */
@RestController
@RequestMapping("/api")
public class CategoriesResource {

    private final Logger log = LoggerFactory.getLogger(CategoriesResource.class);

    @Inject
    private CategoriesRepository categoriesRepository;

    @Inject
    private CategoriesSearchRepository categoriesSearchRepository;

    /**
     * POST  /categories : Create a new categories.
     *
     * @param categories the categories to create
     * @return the ResponseEntity with status 201 (Created) and with body the new categories, or with status 400 (Bad Request) if the categories has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/categories")
    @Timed
    public ResponseEntity<Categories> createCategories(@Valid @RequestBody Categories categories) throws URISyntaxException {
        log.debug("REST request to save Categories : {}", categories);
        if (categories.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("categories", "idexists", "A new categories cannot already have an ID")).body(null);
        }
        Categories result = categoriesRepository.save(categories);
        categoriesSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/categories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("categories", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /categories : Updates an existing categories.
     *
     * @param categories the categories to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated categories,
     * or with status 400 (Bad Request) if the categories is not valid,
     * or with status 500 (Internal Server Error) if the categories couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/categories")
    @Timed
    public ResponseEntity<Categories> updateCategories(@Valid @RequestBody Categories categories) throws URISyntaxException {
        log.debug("REST request to update Categories : {}", categories);
        if (categories.getId() == null) {
            return createCategories(categories);
        }
        Categories result = categoriesRepository.save(categories);
        categoriesSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("categories", categories.getId().toString()))
            .body(result);
    }

    /**
     * GET  /categories : get all the categories.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of categories in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/categories")
    @Timed
    public ResponseEntity<List<Categories>> getAllCategories(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Categories");
        Page<Categories> page = categoriesRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/categories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /categoriesAll : get all categories NO PAGINATION
     *
     * @return the ResponseEntity with status 200 (OK) and the list of categories in body
     */
    @GetMapping("/categoriesAll")
    @Timed
    public ResponseEntity<List<Categories>> getAllCategories() {
        log.debug("REST request to get all Categories (no pagination)");
        List<Categories> result = categoriesRepository.findAll();

        if (result.size() > 0) {
            return ResponseEntity.ok().headers(HeaderUtil.createAlert("Results found", "Retrieved all categories")).body(result);
        } else {
            return ResponseEntity.status(404).headers(HeaderUtil.createAlert("No results found", "Error retrieving categories")).body(null);
        }
    }

    /**
     * GET  /categories/:id : get the "id" categories.
     *
     * @param id the id of the categories to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the categories, or with status 404 (Not Found)
     */
    @GetMapping("/categories/{id}")
    @Timed
    public ResponseEntity<Categories> getCategories(@PathVariable Long id) {
        log.debug("REST request to get Categories : {}", id);
        Categories categories = categoriesRepository.findOne(id);
        return Optional.ofNullable(categories)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /categories/:id : delete the "id" categories.
     *
     * @param id the id of the categories to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/categories/{id}")
    @Timed
    public ResponseEntity<Void> deleteCategories(@PathVariable Long id) {
        log.debug("REST request to delete Categories : {}", id);
        categoriesRepository.delete(id);
        categoriesSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("categories", id.toString())).build();
    }

    /**
     * SEARCH  /_search/categories?query=:query : search for the categories corresponding
     * to the query.
     *
     * @param query the query of the categories search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/categories")
    @Timed
    public ResponseEntity<List<Categories>> searchCategories(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Categories for query {}", query);
        Page<Categories> page = categoriesSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/categories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
