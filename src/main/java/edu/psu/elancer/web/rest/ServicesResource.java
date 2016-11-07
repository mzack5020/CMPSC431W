package edu.psu.elancer.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.psu.elancer.domain.Services;

import edu.psu.elancer.repository.ServicesRepository;
import edu.psu.elancer.repository.search.ServicesSearchRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Services.
 */
@RestController
@RequestMapping("/api")
public class ServicesResource {

    private final Logger log = LoggerFactory.getLogger(ServicesResource.class);

    @Inject
    private ServicesRepository servicesRepository;

    @Inject
    private ServicesSearchRepository servicesSearchRepository;

    /**
     * POST  /services : Create a new services.
     *
     * @param services the services to create
     * @return the ResponseEntity with status 201 (Created) and with body the new services, or with status 400 (Bad Request) if the services has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/services")
    @Timed
    public ResponseEntity<Services> createServices(@Valid @RequestBody Services services) throws URISyntaxException {
        log.debug("REST request to save Services : {}", services);
        if (services.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("services", "idexists", "A new services cannot already have an ID")).body(null);
        }
        services.setDatePosted(LocalDate.now());
        services.completed(false);
        Services result = servicesRepository.save(services);
        servicesSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/services/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("services", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /services : Updates an existing services.
     *
     * @param services the services to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated services,
     * or with status 400 (Bad Request) if the services is not valid,
     * or with status 500 (Internal Server Error) if the services couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/services")
    @Timed
    public ResponseEntity<Services> updateServices(@Valid @RequestBody Services services) throws URISyntaxException {
        log.debug("REST request to update Services : {}", services);
        if (services.getId() == null) {
            return createServices(services);
        }
        Services result = servicesRepository.save(services);
        servicesSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("services", services.getId().toString()))
            .body(result);
    }

    /**
     * GET  /services : get all the services.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of services in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/services")
    @Timed
    public ResponseEntity<List<Services>> getAllServices(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Services");
        Page<Services> page = servicesRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/services");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /services/:id : get the "id" services.
     *
     * @param id the id of the services to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the services, or with status 404 (Not Found)
     */
    @GetMapping("/services/{id}")
    @Timed
    public ResponseEntity<Services> getServices(@PathVariable Long id) {
        log.debug("REST request to get Services : {}", id);
        Services services = servicesRepository.findOne(id);
        return Optional.ofNullable(services)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /services/:id : delete the "id" services.
     *
     * @param id the id of the services to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/services/{id}")
    @Timed
    public ResponseEntity<Void> deleteServices(@PathVariable Long id) {
        log.debug("REST request to delete Services : {}", id);
        servicesRepository.delete(id);
        servicesSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("services", id.toString())).build();
    }

    /**
     * SEARCH  /_search/services?query=:query : search for the services corresponding
     * to the query.
     *
     * @param query the query of the services search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/services")
    @Timed
    public ResponseEntity<List<Services>> searchServices(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Services for query {}", query);
        Page<Services> page = servicesSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/services");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     *  GET  /profile/services/{id}
     *
     *  @param id : id of profile to get services for
     *  @return the result of the search Status 200 (OK)
     */
    @GetMapping("/profile/services/{id}")
    @Timed
    public ResponseEntity<List<Services>> findServicesByCustomerId(@PathVariable Long id) {
        log.debug("REST request to get all services for user");
        List<Services> response = servicesRepository.findByCustomerId(id);
        log.debug("SERVICES FOUND: ");
        log.debug(response.toString());
        return ResponseEntity.ok().body(response);
    }

}
