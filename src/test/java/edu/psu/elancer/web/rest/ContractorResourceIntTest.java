package edu.psu.elancer.web.rest;

import edu.psu.elancer.ELancerApp;

import edu.psu.elancer.domain.Contractor;
import edu.psu.elancer.repository.ContractorRepository;
import edu.psu.elancer.repository.search.ContractorSearchRepository;

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
 * Test class for the ContractorResource REST controller.
 *
 * @see ContractorResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ELancerApp.class)
public class ContractorResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final String DEFAULT_EMAIL = "AAAAA";
    private static final String UPDATED_EMAIL = "BBBBB";

    private static final String DEFAULT_PHONE = "AAAAA";
    private static final String UPDATED_PHONE = "BBBBB";

    private static final Double DEFAULT_RATING = 1D;
    private static final Double UPDATED_RATING = 2D;

    @Inject
    private ContractorRepository contractorRepository;

    @Inject
    private ContractorSearchRepository contractorSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restContractorMockMvc;

    private Contractor contractor;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ContractorResource contractorResource = new ContractorResource();
        ReflectionTestUtils.setField(contractorResource, "contractorSearchRepository", contractorSearchRepository);
        ReflectionTestUtils.setField(contractorResource, "contractorRepository", contractorRepository);
        this.restContractorMockMvc = MockMvcBuilders.standaloneSetup(contractorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Contractor createEntity(EntityManager em) {
        Contractor contractor = new Contractor()
                .name(DEFAULT_NAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING);
        return contractor;
    }

    @Before
    public void initTest() {
        contractorSearchRepository.deleteAll();
        contractor = createEntity(em);
    }

    @Test
    @Transactional
    public void createContractor() throws Exception {
        int databaseSizeBeforeCreate = contractorRepository.findAll().size();

        // Create the Contractor

        restContractorMockMvc.perform(post("/api/contractors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contractor)))
                .andExpect(status().isCreated());

        // Validate the Contractor in the database
        List<Contractor> contractors = contractorRepository.findAll();
        assertThat(contractors).hasSize(databaseSizeBeforeCreate + 1);
        Contractor testContractor = contractors.get(contractors.size() - 1);
        assertThat(testContractor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testContractor.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testContractor.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testContractor.getRating()).isEqualTo(DEFAULT_RATING);

        // Validate the Contractor in ElasticSearch
        Contractor contractorEs = contractorSearchRepository.findOne(testContractor.getId());
        assertThat(contractorEs).isEqualToComparingFieldByField(testContractor);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = contractorRepository.findAll().size();
        // set the field null
        contractor.setName(null);

        // Create the Contractor, which fails.

        restContractorMockMvc.perform(post("/api/contractors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contractor)))
                .andExpect(status().isBadRequest());

        List<Contractor> contractors = contractorRepository.findAll();
        assertThat(contractors).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = contractorRepository.findAll().size();
        // set the field null
        contractor.setEmail(null);

        // Create the Contractor, which fails.

        restContractorMockMvc.perform(post("/api/contractors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contractor)))
                .andExpect(status().isBadRequest());

        List<Contractor> contractors = contractorRepository.findAll();
        assertThat(contractors).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllContractors() throws Exception {
        // Initialize the database
        contractorRepository.saveAndFlush(contractor);

        // Get all the contractors
        restContractorMockMvc.perform(get("/api/contractors?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(contractor.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
                .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE.toString())))
                .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING.doubleValue())));
    }

    @Test
    @Transactional
    public void getContractor() throws Exception {
        // Initialize the database
        contractorRepository.saveAndFlush(contractor);

        // Get the contractor
        restContractorMockMvc.perform(get("/api/contractors/{id}", contractor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(contractor.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE.toString()))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingContractor() throws Exception {
        // Get the contractor
        restContractorMockMvc.perform(get("/api/contractors/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateContractor() throws Exception {
        // Initialize the database
        contractorRepository.saveAndFlush(contractor);
        contractorSearchRepository.save(contractor);
        int databaseSizeBeforeUpdate = contractorRepository.findAll().size();

        // Update the contractor
        Contractor updatedContractor = contractorRepository.findOne(contractor.getId());
        updatedContractor
                .name(UPDATED_NAME)
                .email(UPDATED_EMAIL)
                .phone(UPDATED_PHONE)
                .rating(UPDATED_RATING);

        restContractorMockMvc.perform(put("/api/contractors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedContractor)))
                .andExpect(status().isOk());

        // Validate the Contractor in the database
        List<Contractor> contractors = contractorRepository.findAll();
        assertThat(contractors).hasSize(databaseSizeBeforeUpdate);
        Contractor testContractor = contractors.get(contractors.size() - 1);
        assertThat(testContractor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testContractor.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testContractor.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testContractor.getRating()).isEqualTo(UPDATED_RATING);

        // Validate the Contractor in ElasticSearch
        Contractor contractorEs = contractorSearchRepository.findOne(testContractor.getId());
        assertThat(contractorEs).isEqualToComparingFieldByField(testContractor);
    }

    @Test
    @Transactional
    public void deleteContractor() throws Exception {
        // Initialize the database
        contractorRepository.saveAndFlush(contractor);
        contractorSearchRepository.save(contractor);
        int databaseSizeBeforeDelete = contractorRepository.findAll().size();

        // Get the contractor
        restContractorMockMvc.perform(delete("/api/contractors/{id}", contractor.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean contractorExistsInEs = contractorSearchRepository.exists(contractor.getId());
        assertThat(contractorExistsInEs).isFalse();

        // Validate the database is empty
        List<Contractor> contractors = contractorRepository.findAll();
        assertThat(contractors).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchContractor() throws Exception {
        // Initialize the database
        contractorRepository.saveAndFlush(contractor);
        contractorSearchRepository.save(contractor);

        // Search the contractor
        restContractorMockMvc.perform(get("/api/_search/contractors?query=id:" + contractor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contractor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE.toString())))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING.doubleValue())));
    }
}
