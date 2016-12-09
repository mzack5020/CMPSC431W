package edu.psu.elancer.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.psu.elancer.domain.Bids;

import edu.psu.elancer.domain.Contractor;
import edu.psu.elancer.domain.Customer;
import edu.psu.elancer.domain.Services;
import edu.psu.elancer.repository.BidsRepository;
import edu.psu.elancer.repository.ContractorRepository;
import edu.psu.elancer.repository.CustomerRepository;
import edu.psu.elancer.repository.ServicesRepository;
import edu.psu.elancer.repository.search.BidsSearchRepository;
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
import javax.sql.DataSource;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Bids.
 */
@RestController
@RequestMapping("/api")
public class BidsResource {

    private final Logger log = LoggerFactory.getLogger(BidsResource.class);

    @Inject
    private DataSource dataSource;

    @Inject
    private BidsRepository bidsRepository;

    @Inject
    private ServicesRepository servicesRepository;

    @Inject
    private ContractorRepository contractorRepository;

    @Inject
    private BidsSearchRepository bidsSearchRepository;

    /**
     * POST  /bids : Create a new bids.
     *
     * @param bids the bids to create
     * @return the ResponseEntity with status 201 (Created) and with body the new bids, or with status 400 (Bad Request) if the bids has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/bids")
    @Timed
    public ResponseEntity<Bids> createBids(@Valid @RequestBody Bids bids) throws URISyntaxException {
        log.debug("REST request to save Bids : {}", bids);
        if (bids.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("bids", "idexists", "A new bids cannot already have an ID")).body(null);
        }
        Bids result = bidsRepository.save(bids);
        bidsSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/bids/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("bids", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /bids : Updates an existing bids.
     *
     * @param bids the bids to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated bids,
     * or with status 400 (Bad Request) if the bids is not valid,
     * or with status 500 (Internal Server Error) if the bids couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/bids")
    @Timed
    public ResponseEntity<Bids> updateBids(@Valid @RequestBody Bids bids) throws URISyntaxException {
        log.debug("REST request to update Bids : {}", bids);
        if (bids.getId() == null) {
            return createBids(bids);
        }
        Bids result = bidsRepository.save(bids);
        bidsSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("bids", bids.getId().toString()))
            .body(result);
    }

    /**
     * GET  /bids : get all the bids.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of bids in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/bids")
    @Timed
    public ResponseEntity<List<Bids>> getAllBids(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Bids");
        Page<Bids> page = bidsRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bids");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /bids/:id : get the "id" bids.
     *
     * @param id the id of the bids to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the bids, or with status 404 (Not Found)
     */
    @GetMapping("/bids/{id}")
    @Timed
    public ResponseEntity<Bids> getBids(@PathVariable Long id) {
        log.debug("REST request to get Bids : {}", id);
        Bids bids = bidsRepository.findOne(id);
        return Optional.ofNullable(bids)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /bids/:id : delete the "id" bids.
     *
     * @param id the id of the bids to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/bids/{id}")
    @Timed
    public ResponseEntity<Void> deleteBids(@PathVariable Long id) {
        log.debug("REST request to delete Bids : {}", id);
        bidsRepository.delete(id);
        bidsSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("bids", id.toString())).build();
    }

    /**
     * SEARCH  /_search/bids?query=:query : search for the bids corresponding
     * to the query.
     *
     * @param query the query of the bids search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/bids")
    @Timed
    public ResponseEntity<List<Bids>> searchBids(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Bids for query {}", query);
        Page<Bids> page = bidsSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/bids");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /sorted-bids/{id}
     *
     * @param id : id of the service to get the bids for
     * @return list of bids for that service with Status 200 (OK)
     */
    @GetMapping("/sorted-bids/{id}")
    @Timed
    public ResponseEntity<List<Bids>> getBidsForService(@PathVariable Long id) {
        Services service = servicesRepository.findOne(id);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Bids> result = new ArrayList<Bids>();
        try {
            connection = dataSource.getConnection();
            String queryStatement = "SELECT * FROM Bids WHERE `services_id` = '" + id + "';";
            preparedStatement = connection.prepareStatement(queryStatement);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                Bids newBid = new Bids();
                newBid.setId(resultSet.getLong("id"));
                newBid.setAmount(resultSet.getString("amount"));
                newBid.setServices(service);

                Contractor contractor = contractorRepository.findOne(resultSet.getLong("contractor_id"));
                newBid.setContractor(contractor);
                result.add(newBid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return ResponseEntity.ok().headers(HeaderUtil.createAlert("Queried against database", "Contractor id: " + id)).body(result);
    }

}
