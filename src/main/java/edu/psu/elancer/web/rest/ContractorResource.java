package edu.psu.elancer.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.psu.elancer.domain.Contractor;

import edu.psu.elancer.repository.ContractorRepository;
import edu.psu.elancer.repository.search.ContractorSearchRepository;
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
 * REST controller for managing Contractor.
 */
@RestController
@RequestMapping("/api")
public class ContractorResource {

    private final Logger log = LoggerFactory.getLogger(ContractorResource.class);
        
    @Inject
    private ContractorRepository contractorRepository;

    @Inject
    private ContractorSearchRepository contractorSearchRepository;

    /**
     * POST  /contractors : Create a new contractor.
     *
     * @param contractor the contractor to create
     * @return the ResponseEntity with status 201 (Created) and with body the new contractor, or with status 400 (Bad Request) if the contractor has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/contractors")
    @Timed
    public ResponseEntity<Contractor> createContractor(@Valid @RequestBody Contractor contractor) throws URISyntaxException {
        log.debug("REST request to save Contractor : {}", contractor);
        if (contractor.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("contractor", "idexists", "A new contractor cannot already have an ID")).body(null);
        }
        Contractor result = contractorRepository.save(contractor);
        contractorSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/contractors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("contractor", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /contractors : Updates an existing contractor.
     *
     * @param contractor the contractor to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated contractor,
     * or with status 400 (Bad Request) if the contractor is not valid,
     * or with status 500 (Internal Server Error) if the contractor couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/contractors")
    @Timed
    public ResponseEntity<Contractor> updateContractor(@Valid @RequestBody Contractor contractor) throws URISyntaxException {
        log.debug("REST request to update Contractor : {}", contractor);
        if (contractor.getId() == null) {
            return createContractor(contractor);
        }
        Contractor result = contractorRepository.save(contractor);
        contractorSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("contractor", contractor.getId().toString()))
            .body(result);
    }

    /**
     * GET  /contractors : get all the contractors.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of contractors in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/contractors")
    @Timed
    public ResponseEntity<List<Contractor>> getAllContractors(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Contractors");
        Page<Contractor> page = contractorRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contractors");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /contractors/:id : get the "id" contractor.
     *
     * @param id the id of the contractor to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the contractor, or with status 404 (Not Found)
     */
    @GetMapping("/contractors/{id}")
    @Timed
    public ResponseEntity<Contractor> getContractor(@PathVariable Long id) {
        log.debug("REST request to get Contractor : {}", id);
        Contractor contractor = contractorRepository.findOne(id);
        return Optional.ofNullable(contractor)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /contractors/:id : delete the "id" contractor.
     *
     * @param id the id of the contractor to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/contractors/{id}")
    @Timed
    public ResponseEntity<Void> deleteContractor(@PathVariable Long id) {
        log.debug("REST request to delete Contractor : {}", id);
        contractorRepository.delete(id);
        contractorSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contractor", id.toString())).build();
    }

    /**
     * SEARCH  /_search/contractors?query=:query : search for the contractor corresponding
     * to the query.
     *
     * @param query the query of the contractor search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/contractors")
    @Timed
    public ResponseEntity<List<Contractor>> searchContractors(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Contractors for query {}", query);
        Page<Contractor> page = contractorSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/contractors");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
