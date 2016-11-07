package edu.psu.elancer.web.rest;

import edu.psu.elancer.ELancerApp;

import edu.psu.elancer.domain.Bids;
import edu.psu.elancer.domain.Services;
import edu.psu.elancer.domain.Contractor;
import edu.psu.elancer.repository.BidsRepository;
import edu.psu.elancer.repository.search.BidsSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BidsResource REST controller.
 *
 * @see BidsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ELancerApp.class)
public class BidsResourceIntTest {

    private static final String DEFAULT_AMOUNT = "AAAAA";
    private static final String UPDATED_AMOUNT = "BBBBB";

    @Inject
    private BidsRepository bidsRepository;

    @Inject
    private BidsSearchRepository bidsSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restBidsMockMvc;

    private Bids bids;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BidsResource bidsResource = new BidsResource();
        ReflectionTestUtils.setField(bidsResource, "bidsSearchRepository", bidsSearchRepository);
        ReflectionTestUtils.setField(bidsResource, "bidsRepository", bidsRepository);
        this.restBidsMockMvc = MockMvcBuilders.standaloneSetup(bidsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bids createEntity(EntityManager em) {
        Bids bids = new Bids()
                .amount(DEFAULT_AMOUNT);
        // Add required entity
        Services services = ServicesResourceIntTest.createEntity(em);
        em.persist(services);
        em.flush();
        bids.setServices(services);
        // Add required entity
        Contractor contractor = ContractorResourceIntTest.createEntity(em);
        em.persist(contractor);
        em.flush();
        bids.setContractor(contractor);
        return bids;
    }

    @Before
    public void initTest() {
        bidsSearchRepository.deleteAll();
        bids = createEntity(em);
    }

    @Test
    @Transactional
    public void createBids() throws Exception {
        int databaseSizeBeforeCreate = bidsRepository.findAll().size();

        // Create the Bids

        restBidsMockMvc.perform(post("/api/bids")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bids)))
                .andExpect(status().isCreated());

        // Validate the Bids in the database
        List<Bids> bids = bidsRepository.findAll();
        assertThat(bids).hasSize(databaseSizeBeforeCreate + 1);
        Bids testBids = bids.get(bids.size() - 1);
        assertThat(testBids.getAmount()).isEqualTo(DEFAULT_AMOUNT);

        // Validate the Bids in ElasticSearch
        Bids bidsEs = bidsSearchRepository.findOne(testBids.getId());
        assertThat(bidsEs).isEqualToComparingFieldByField(testBids);
    }

    @Test
    @Transactional
    public void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = bidsRepository.findAll().size();
        // set the field null
        bids.setAmount(null);

        // Create the Bids, which fails.

        restBidsMockMvc.perform(post("/api/bids")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bids)))
                .andExpect(status().isBadRequest());

        List<Bids> bids = bidsRepository.findAll();
        assertThat(bids).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBids() throws Exception {
        // Initialize the database
        bidsRepository.saveAndFlush(bids);

        // Get all the bids
        restBidsMockMvc.perform(get("/api/bids?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(bids.getId().intValue())))
                .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.toString())));
    }

    @Test
    @Transactional
    public void getBids() throws Exception {
        // Initialize the database
        bidsRepository.saveAndFlush(bids);

        // Get the bids
        restBidsMockMvc.perform(get("/api/bids/{id}", bids.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(bids.getId().intValue()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBids() throws Exception {
        // Get the bids
        restBidsMockMvc.perform(get("/api/bids/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBids() throws Exception {
        // Initialize the database
        bidsRepository.saveAndFlush(bids);
        bidsSearchRepository.save(bids);
        int databaseSizeBeforeUpdate = bidsRepository.findAll().size();

        // Update the bids
        Bids updatedBids = bidsRepository.findOne(bids.getId());
        updatedBids
                .amount(UPDATED_AMOUNT);

        restBidsMockMvc.perform(put("/api/bids")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedBids)))
                .andExpect(status().isOk());

        // Validate the Bids in the database
        List<Bids> bids = bidsRepository.findAll();
        assertThat(bids).hasSize(databaseSizeBeforeUpdate);
        Bids testBids = bids.get(bids.size() - 1);
        assertThat(testBids.getAmount()).isEqualTo(UPDATED_AMOUNT);

        // Validate the Bids in ElasticSearch
        Bids bidsEs = bidsSearchRepository.findOne(testBids.getId());
        assertThat(bidsEs).isEqualToComparingFieldByField(testBids);
    }

    @Test
    @Transactional
    public void deleteBids() throws Exception {
        // Initialize the database
        bidsRepository.saveAndFlush(bids);
        bidsSearchRepository.save(bids);
        int databaseSizeBeforeDelete = bidsRepository.findAll().size();

        // Get the bids
        restBidsMockMvc.perform(delete("/api/bids/{id}", bids.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean bidsExistsInEs = bidsSearchRepository.exists(bids.getId());
        assertThat(bidsExistsInEs).isFalse();

        // Validate the database is empty
        List<Bids> bids = bidsRepository.findAll();
        assertThat(bids).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBids() throws Exception {
        // Initialize the database
        bidsRepository.saveAndFlush(bids);
        bidsSearchRepository.save(bids);

        // Search the bids
        restBidsMockMvc.perform(get("/api/_search/bids?query=id:" + bids.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bids.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.toString())));
    }
}
