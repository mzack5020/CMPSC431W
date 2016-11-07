package edu.psu.elancer.web.rest;

import edu.psu.elancer.ELancerApp;

import edu.psu.elancer.domain.Services;
import edu.psu.elancer.domain.Customer;
import edu.psu.elancer.domain.Categories;
import edu.psu.elancer.repository.ServicesRepository;
import edu.psu.elancer.repository.search.ServicesSearchRepository;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ServicesResource REST controller.
 *
 * @see ServicesResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ELancerApp.class)
public class ServicesResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final Integer DEFAULT_LOCATION_ZIP = 10000;
    private static final Integer UPDATED_LOCATION_ZIP = 10001;

    private static final LocalDate DEFAULT_DATE_POSTED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_POSTED = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_REPORTED_COUNT = 1;
    private static final Integer UPDATED_REPORTED_COUNT = 2;

    private static final String DEFAULT_PHOTO_PATH = "AAAAA";
    private static final String UPDATED_PHOTO_PATH = "BBBBB";

    private static final LocalDate DEFAULT_EXPIRATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EXPIRATION_DATE = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private ServicesRepository servicesRepository;

    @Inject
    private ServicesSearchRepository servicesSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restServicesMockMvc;

    private Services services;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ServicesResource servicesResource = new ServicesResource();
        ReflectionTestUtils.setField(servicesResource, "servicesSearchRepository", servicesSearchRepository);
        ReflectionTestUtils.setField(servicesResource, "servicesRepository", servicesRepository);
        this.restServicesMockMvc = MockMvcBuilders.standaloneSetup(servicesResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Services createEntity(EntityManager em) {
        Services services = new Services()
                .name(DEFAULT_NAME)
                .description(DEFAULT_DESCRIPTION)
                .locationZip(DEFAULT_LOCATION_ZIP)
                .datePosted(DEFAULT_DATE_POSTED)
                .reportedCount(DEFAULT_REPORTED_COUNT)
                .photoPath(DEFAULT_PHOTO_PATH)
                .expirationDate(DEFAULT_EXPIRATION_DATE);
        // Add required entity
        Customer customer = CustomerResourceIntTest.createEntity(em);
        em.persist(customer);
        em.flush();
        services.setCustomer(customer);
        // Add required entity
        Categories categories = CategoriesResourceIntTest.createEntity(em);
        em.persist(categories);
        em.flush();
        services.setCategories(categories);
        return services;
    }

    @Before
    public void initTest() {
        servicesSearchRepository.deleteAll();
        services = createEntity(em);
    }

    @Test
    @Transactional
    public void createServices() throws Exception {
        int databaseSizeBeforeCreate = servicesRepository.findAll().size();

        // Create the Services

        restServicesMockMvc.perform(post("/api/services")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(services)))
                .andExpect(status().isCreated());

        // Validate the Services in the database
        List<Services> services = servicesRepository.findAll();
        assertThat(services).hasSize(databaseSizeBeforeCreate + 1);
        Services testServices = services.get(services.size() - 1);
        assertThat(testServices.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testServices.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testServices.getLocationZip()).isEqualTo(DEFAULT_LOCATION_ZIP);
        assertThat(testServices.getDatePosted()).isEqualTo(DEFAULT_DATE_POSTED);
        assertThat(testServices.getReportedCount()).isEqualTo(DEFAULT_REPORTED_COUNT);
        assertThat(testServices.getPhotoPath()).isEqualTo(DEFAULT_PHOTO_PATH);
        assertThat(testServices.getExpirationDate()).isEqualTo(DEFAULT_EXPIRATION_DATE);

        // Validate the Services in ElasticSearch
        Services servicesEs = servicesSearchRepository.findOne(testServices.getId());
        assertThat(servicesEs).isEqualToComparingFieldByField(testServices);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = servicesRepository.findAll().size();
        // set the field null
        services.setName(null);

        // Create the Services, which fails.

        restServicesMockMvc.perform(post("/api/services")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(services)))
                .andExpect(status().isBadRequest());

        List<Services> services = servicesRepository.findAll();
        assertThat(services).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = servicesRepository.findAll().size();
        // set the field null
        services.setDescription(null);

        // Create the Services, which fails.

        restServicesMockMvc.perform(post("/api/services")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(services)))
                .andExpect(status().isBadRequest());

        List<Services> services = servicesRepository.findAll();
        assertThat(services).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLocationZipIsRequired() throws Exception {
        int databaseSizeBeforeTest = servicesRepository.findAll().size();
        // set the field null
        services.setLocationZip(null);

        // Create the Services, which fails.

        restServicesMockMvc.perform(post("/api/services")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(services)))
                .andExpect(status().isBadRequest());

        List<Services> services = servicesRepository.findAll();
        assertThat(services).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkExpirationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = servicesRepository.findAll().size();
        // set the field null
        services.setExpirationDate(null);

        // Create the Services, which fails.

        restServicesMockMvc.perform(post("/api/services")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(services)))
                .andExpect(status().isBadRequest());

        List<Services> services = servicesRepository.findAll();
        assertThat(services).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllServices() throws Exception {
        // Initialize the database
        servicesRepository.saveAndFlush(services);

        // Get all the services
        restServicesMockMvc.perform(get("/api/services?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(services.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].locationZip").value(hasItem(DEFAULT_LOCATION_ZIP)))
                .andExpect(jsonPath("$.[*].datePosted").value(hasItem(DEFAULT_DATE_POSTED.toString())))
                .andExpect(jsonPath("$.[*].reportedCount").value(hasItem(DEFAULT_REPORTED_COUNT)))
                .andExpect(jsonPath("$.[*].photoPath").value(hasItem(DEFAULT_PHOTO_PATH.toString())))
                .andExpect(jsonPath("$.[*].expirationDate").value(hasItem(DEFAULT_EXPIRATION_DATE.toString())));
    }

    @Test
    @Transactional
    public void getServices() throws Exception {
        // Initialize the database
        servicesRepository.saveAndFlush(services);

        // Get the services
        restServicesMockMvc.perform(get("/api/services/{id}", services.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(services.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.locationZip").value(DEFAULT_LOCATION_ZIP))
            .andExpect(jsonPath("$.datePosted").value(DEFAULT_DATE_POSTED.toString()))
            .andExpect(jsonPath("$.reportedCount").value(DEFAULT_REPORTED_COUNT))
            .andExpect(jsonPath("$.photoPath").value(DEFAULT_PHOTO_PATH.toString()))
            .andExpect(jsonPath("$.expirationDate").value(DEFAULT_EXPIRATION_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingServices() throws Exception {
        // Get the services
        restServicesMockMvc.perform(get("/api/services/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateServices() throws Exception {
        // Initialize the database
        servicesRepository.saveAndFlush(services);
        servicesSearchRepository.save(services);
        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();

        // Update the services
        Services updatedServices = servicesRepository.findOne(services.getId());
        updatedServices
                .name(UPDATED_NAME)
                .description(UPDATED_DESCRIPTION)
                .locationZip(UPDATED_LOCATION_ZIP)
                .datePosted(UPDATED_DATE_POSTED)
                .reportedCount(UPDATED_REPORTED_COUNT)
                .photoPath(UPDATED_PHOTO_PATH)
                .expirationDate(UPDATED_EXPIRATION_DATE);

        restServicesMockMvc.perform(put("/api/services")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedServices)))
                .andExpect(status().isOk());

        // Validate the Services in the database
        List<Services> services = servicesRepository.findAll();
        assertThat(services).hasSize(databaseSizeBeforeUpdate);
        Services testServices = services.get(services.size() - 1);
        assertThat(testServices.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testServices.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testServices.getLocationZip()).isEqualTo(UPDATED_LOCATION_ZIP);
        assertThat(testServices.getDatePosted()).isEqualTo(UPDATED_DATE_POSTED);
        assertThat(testServices.getReportedCount()).isEqualTo(UPDATED_REPORTED_COUNT);
        assertThat(testServices.getPhotoPath()).isEqualTo(UPDATED_PHOTO_PATH);
        assertThat(testServices.getExpirationDate()).isEqualTo(UPDATED_EXPIRATION_DATE);

        // Validate the Services in ElasticSearch
        Services servicesEs = servicesSearchRepository.findOne(testServices.getId());
        assertThat(servicesEs).isEqualToComparingFieldByField(testServices);
    }

    @Test
    @Transactional
    public void deleteServices() throws Exception {
        // Initialize the database
        servicesRepository.saveAndFlush(services);
        servicesSearchRepository.save(services);
        int databaseSizeBeforeDelete = servicesRepository.findAll().size();

        // Get the services
        restServicesMockMvc.perform(delete("/api/services/{id}", services.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean servicesExistsInEs = servicesSearchRepository.exists(services.getId());
        assertThat(servicesExistsInEs).isFalse();

        // Validate the database is empty
        List<Services> services = servicesRepository.findAll();
        assertThat(services).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchServices() throws Exception {
        // Initialize the database
        servicesRepository.saveAndFlush(services);
        servicesSearchRepository.save(services);

        // Search the services
        restServicesMockMvc.perform(get("/api/_search/services?query=id:" + services.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(services.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].locationZip").value(hasItem(DEFAULT_LOCATION_ZIP)))
            .andExpect(jsonPath("$.[*].datePosted").value(hasItem(DEFAULT_DATE_POSTED.toString())))
            .andExpect(jsonPath("$.[*].reportedCount").value(hasItem(DEFAULT_REPORTED_COUNT)))
            .andExpect(jsonPath("$.[*].photoPath").value(hasItem(DEFAULT_PHOTO_PATH.toString())))
            .andExpect(jsonPath("$.[*].expirationDate").value(hasItem(DEFAULT_EXPIRATION_DATE.toString())));
    }
}
