package edu.psu.elancer.web.rest;

import edu.psu.elancer.ELancerApp;

import edu.psu.elancer.domain.Categories;
import edu.psu.elancer.repository.CategoriesRepository;
import edu.psu.elancer.repository.search.CategoriesSearchRepository;

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
 * Test class for the CategoriesResource REST controller.
 *
 * @see CategoriesResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ELancerApp.class)
public class CategoriesResourceIntTest {

    private static final String DEFAULT_CATEGORY = "AAAAA";
    private static final String UPDATED_CATEGORY = "BBBBB";

    @Inject
    private CategoriesRepository categoriesRepository;

    @Inject
    private CategoriesSearchRepository categoriesSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restCategoriesMockMvc;

    private Categories categories;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CategoriesResource categoriesResource = new CategoriesResource();
        ReflectionTestUtils.setField(categoriesResource, "categoriesSearchRepository", categoriesSearchRepository);
        ReflectionTestUtils.setField(categoriesResource, "categoriesRepository", categoriesRepository);
        this.restCategoriesMockMvc = MockMvcBuilders.standaloneSetup(categoriesResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Categories createEntity(EntityManager em) {
        Categories categories = new Categories()
                .category(DEFAULT_CATEGORY);
        return categories;
    }

    @Before
    public void initTest() {
        categoriesSearchRepository.deleteAll();
        categories = createEntity(em);
    }

    @Test
    @Transactional
    public void createCategories() throws Exception {
        int databaseSizeBeforeCreate = categoriesRepository.findAll().size();

        // Create the Categories

        restCategoriesMockMvc.perform(post("/api/categories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(categories)))
                .andExpect(status().isCreated());

        // Validate the Categories in the database
        List<Categories> categories = categoriesRepository.findAll();
        assertThat(categories).hasSize(databaseSizeBeforeCreate + 1);
        Categories testCategories = categories.get(categories.size() - 1);
        assertThat(testCategories.getCategory()).isEqualTo(DEFAULT_CATEGORY);

        // Validate the Categories in ElasticSearch
        Categories categoriesEs = categoriesSearchRepository.findOne(testCategories.getId());
        assertThat(categoriesEs).isEqualToComparingFieldByField(testCategories);
    }

    @Test
    @Transactional
    public void checkCategoryIsRequired() throws Exception {
        int databaseSizeBeforeTest = categoriesRepository.findAll().size();
        // set the field null
        categories.setCategory(null);

        // Create the Categories, which fails.

        restCategoriesMockMvc.perform(post("/api/categories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(categories)))
                .andExpect(status().isBadRequest());

        List<Categories> categories = categoriesRepository.findAll();
        assertThat(categories).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCategories() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);

        // Get all the categories
        restCategoriesMockMvc.perform(get("/api/categories?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(categories.getId().intValue())))
                .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())));
    }

    @Test
    @Transactional
    public void getCategories() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);

        // Get the categories
        restCategoriesMockMvc.perform(get("/api/categories/{id}", categories.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(categories.getId().intValue()))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCategories() throws Exception {
        // Get the categories
        restCategoriesMockMvc.perform(get("/api/categories/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCategories() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);
        categoriesSearchRepository.save(categories);
        int databaseSizeBeforeUpdate = categoriesRepository.findAll().size();

        // Update the categories
        Categories updatedCategories = categoriesRepository.findOne(categories.getId());
        updatedCategories
                .category(UPDATED_CATEGORY);

        restCategoriesMockMvc.perform(put("/api/categories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCategories)))
                .andExpect(status().isOk());

        // Validate the Categories in the database
        List<Categories> categories = categoriesRepository.findAll();
        assertThat(categories).hasSize(databaseSizeBeforeUpdate);
        Categories testCategories = categories.get(categories.size() - 1);
        assertThat(testCategories.getCategory()).isEqualTo(UPDATED_CATEGORY);

        // Validate the Categories in ElasticSearch
        Categories categoriesEs = categoriesSearchRepository.findOne(testCategories.getId());
        assertThat(categoriesEs).isEqualToComparingFieldByField(testCategories);
    }

    @Test
    @Transactional
    public void deleteCategories() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);
        categoriesSearchRepository.save(categories);
        int databaseSizeBeforeDelete = categoriesRepository.findAll().size();

        // Get the categories
        restCategoriesMockMvc.perform(delete("/api/categories/{id}", categories.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean categoriesExistsInEs = categoriesSearchRepository.exists(categories.getId());
        assertThat(categoriesExistsInEs).isFalse();

        // Validate the database is empty
        List<Categories> categories = categoriesRepository.findAll();
        assertThat(categories).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchCategories() throws Exception {
        // Initialize the database
        categoriesRepository.saveAndFlush(categories);
        categoriesSearchRepository.save(categories);

        // Search the categories
        restCategoriesMockMvc.perform(get("/api/_search/categories?query=id:" + categories.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(categories.getId().intValue())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())));
    }
}
