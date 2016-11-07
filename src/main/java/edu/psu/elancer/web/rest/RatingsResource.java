package edu.psu.elancer.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.psu.elancer.domain.Ratings;

import edu.psu.elancer.repository.RatingsRepository;
import edu.psu.elancer.repository.search.RatingsSearchRepository;
import edu.psu.elancer.web.rest.util.HeaderUtil;
import edu.psu.elancer.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Ratings.
 */
@RestController
@RequestMapping("/api")
public class RatingsResource {

    private final Logger log = LoggerFactory.getLogger(RatingsResource.class);
        
    @Inject
    private RatingsRepository ratingsRepository;

    @Inject
    private RatingsSearchRepository ratingsSearchRepository;

    /**
     * POST  /ratings : Create a new ratings.
     *
     * @param ratings the ratings to create
     * @return the ResponseEntity with status 201 (Created) and with body the new ratings, or with status 400 (Bad Request) if the ratings has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/ratings")
    @Timed
    public ResponseEntity<Ratings> createRatings(@Valid @RequestBody Ratings ratings) throws URISyntaxException {
        log.debug("REST request to save Ratings : {}", ratings);
        if (ratings.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("ratings", "idexists", "A new ratings cannot already have an ID")).body(null);
        }
        Ratings result = ratingsRepository.save(ratings);
        ratingsSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/ratings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("ratings", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /ratings : Updates an existing ratings.
     *
     * @param ratings the ratings to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated ratings,
     * or with status 400 (Bad Request) if the ratings is not valid,
     * or with status 500 (Internal Server Error) if the ratings couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/ratings")
    @Timed
    public ResponseEntity<Ratings> updateRatings(@Valid @RequestBody Ratings ratings) throws URISyntaxException {
        log.debug("REST request to update Ratings : {}", ratings);
        if (ratings.getId() == null) {
            return createRatings(ratings);
        }
        Ratings result = ratingsRepository.save(ratings);
        ratingsSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("ratings", ratings.getId().toString()))
            .body(result);
    }

    /**
     * GET  /ratings : get all the ratings.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of ratings in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/ratings")
    @Timed
    public ResponseEntity<List<Ratings>> getAllRatings(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Ratings");
        Page<Ratings> page = ratingsRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/ratings");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /ratings/:id : get the "id" ratings.
     *
     * @param id the id of the ratings to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the ratings, or with status 404 (Not Found)
     */
    @GetMapping("/ratings/{id}")
    @Timed
    public ResponseEntity<Ratings> getRatings(@PathVariable Long id) {
        log.debug("REST request to get Ratings : {}", id);
        Ratings ratings = ratingsRepository.findOne(id);
        return Optional.ofNullable(ratings)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /ratings/:id : delete the "id" ratings.
     *
     * @param id the id of the ratings to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/ratings/{id}")
    @Timed
    public ResponseEntity<Void> deleteRatings(@PathVariable Long id) {
        log.debug("REST request to delete Ratings : {}", id);
        ratingsRepository.delete(id);
        ratingsSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("ratings", id.toString())).build();
    }

    /**
     * SEARCH  /_search/ratings?query=:query : search for the ratings corresponding
     * to the query.
     *
     * @param query the query of the ratings search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/ratings")
    @Timed
    public ResponseEntity<List<Ratings>> searchRatings(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Ratings for query {}", query);
        Page<Ratings> page = ratingsSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/ratings");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
