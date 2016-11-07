package edu.psu.elancer.web.rest;

import edu.psu.elancer.ELancerApp;

import edu.psu.elancer.domain.Ratings;
import edu.psu.elancer.domain.Customer;
import edu.psu.elancer.domain.Contractor;
import edu.psu.elancer.domain.Services;
import edu.psu.elancer.repository.RatingsRepository;
import edu.psu.elancer.repository.search.RatingsSearchRepository;

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
 * Test class for the RatingsResource REST controller.
 *
 * @see RatingsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ELancerApp.class)
public class RatingsResourceIntTest {

    private static final Double DEFAULT_RATING = 0.0D;
    private static final Double UPDATED_RATING = 1D;

    private static final String DEFAULT_COMMENTS = "AAAAA";
    private static final String UPDATED_COMMENTS = "BBBBB";

    @Inject
    private RatingsRepository ratingsRepository;

    @Inject
    private RatingsSearchRepository ratingsSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restRatingsMockMvc;

    private Ratings ratings;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RatingsResource ratingsResource = new RatingsResource();
        ReflectionTestUtils.setField(ratingsResource, "ratingsSearchRepository", ratingsSearchRepository);
        ReflectionTestUtils.setField(ratingsResource, "ratingsRepository", ratingsRepository);
        this.restRatingsMockMvc = MockMvcBuilders.standaloneSetup(ratingsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ratings createEntity(EntityManager em) {
        Ratings ratings = new Ratings()
                .rating(DEFAULT_RATING)
                .comments(DEFAULT_COMMENTS);
        // Add required entity
        Customer customer = CustomerResourceIntTest.createEntity(em);
        em.persist(customer);
        em.flush();
        ratings.setCustomer(customer);
        // Add required entity
        Contractor contractor = ContractorResourceIntTest.createEntity(em);
        em.persist(contractor);
        em.flush();
        ratings.setContractor(contractor);
        // Add required entity
        Services services = ServicesResourceIntTest.createEntity(em);
        em.persist(services);
        em.flush();
        ratings.setServices(services);
        return ratings;
    }

    @Before
    public void initTest() {
        ratingsSearchRepository.deleteAll();
        ratings = createEntity(em);
    }

    @Test
    @Transactional
    public void createRatings() throws Exception {
        int databaseSizeBeforeCreate = ratingsRepository.findAll().size();

        // Create the Ratings

        restRatingsMockMvc.perform(post("/api/ratings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ratings)))
                .andExpect(status().isCreated());

        // Validate the Ratings in the database
        List<Ratings> ratings = ratingsRepository.findAll();
        assertThat(ratings).hasSize(databaseSizeBeforeCreate + 1);
        Ratings testRatings = ratings.get(ratings.size() - 1);
        assertThat(testRatings.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testRatings.getComments()).isEqualTo(DEFAULT_COMMENTS);

        // Validate the Ratings in ElasticSearch
        Ratings ratingsEs = ratingsSearchRepository.findOne(testRatings.getId());
        assertThat(ratingsEs).isEqualToComparingFieldByField(testRatings);
    }

    @Test
    @Transactional
    public void checkRatingIsRequired() throws Exception {
        int databaseSizeBeforeTest = ratingsRepository.findAll().size();
        // set the field null
        ratings.setRating(null);

        // Create the Ratings, which fails.

        restRatingsMockMvc.perform(post("/api/ratings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ratings)))
                .andExpect(status().isBadRequest());

        List<Ratings> ratings = ratingsRepository.findAll();
        assertThat(ratings).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCommentsIsRequired() throws Exception {
        int databaseSizeBeforeTest = ratingsRepository.findAll().size();
        // set the field null
        ratings.setComments(null);

        // Create the Ratings, which fails.

        restRatingsMockMvc.perform(post("/api/ratings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ratings)))
                .andExpect(status().isBadRequest());

        List<Ratings> ratings = ratingsRepository.findAll();
        assertThat(ratings).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRatings() throws Exception {
        // Initialize the database
        ratingsRepository.saveAndFlush(ratings);

        // Get all the ratings
        restRatingsMockMvc.perform(get("/api/ratings?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(ratings.getId().intValue())))
                .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING.doubleValue())))
                .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS.toString())));
    }

    @Test
    @Transactional
    public void getRatings() throws Exception {
        // Initialize the database
        ratingsRepository.saveAndFlush(ratings);

        // Get the ratings
        restRatingsMockMvc.perform(get("/api/ratings/{id}", ratings.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(ratings.getId().intValue()))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING.doubleValue()))
            .andExpect(jsonPath("$.comments").value(DEFAULT_COMMENTS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRatings() throws Exception {
        // Get the ratings
        restRatingsMockMvc.perform(get("/api/ratings/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRatings() throws Exception {
        // Initialize the database
        ratingsRepository.saveAndFlush(ratings);
        ratingsSearchRepository.save(ratings);
        int databaseSizeBeforeUpdate = ratingsRepository.findAll().size();

        // Update the ratings
        Ratings updatedRatings = ratingsRepository.findOne(ratings.getId());
        updatedRatings
                .rating(UPDATED_RATING)
                .comments(UPDATED_COMMENTS);

        restRatingsMockMvc.perform(put("/api/ratings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedRatings)))
                .andExpect(status().isOk());

        // Validate the Ratings in the database
        List<Ratings> ratings = ratingsRepository.findAll();
        assertThat(ratings).hasSize(databaseSizeBeforeUpdate);
        Ratings testRatings = ratings.get(ratings.size() - 1);
        assertThat(testRatings.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testRatings.getComments()).isEqualTo(UPDATED_COMMENTS);

        // Validate the Ratings in ElasticSearch
        Ratings ratingsEs = ratingsSearchRepository.findOne(testRatings.getId());
        assertThat(ratingsEs).isEqualToComparingFieldByField(testRatings);
    }

    @Test
    @Transactional
    public void deleteRatings() throws Exception {
        // Initialize the database
        ratingsRepository.saveAndFlush(ratings);
        ratingsSearchRepository.save(ratings);
        int databaseSizeBeforeDelete = ratingsRepository.findAll().size();

        // Get the ratings
        restRatingsMockMvc.perform(delete("/api/ratings/{id}", ratings.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean ratingsExistsInEs = ratingsSearchRepository.exists(ratings.getId());
        assertThat(ratingsExistsInEs).isFalse();

        // Validate the database is empty
        List<Ratings> ratings = ratingsRepository.findAll();
        assertThat(ratings).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchRatings() throws Exception {
        // Initialize the database
        ratingsRepository.saveAndFlush(ratings);
        ratingsSearchRepository.save(ratings);

        // Search the ratings
        restRatingsMockMvc.perform(get("/api/_search/ratings?query=id:" + ratings.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ratings.getId().intValue())))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING.doubleValue())))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS.toString())));
    }
}
