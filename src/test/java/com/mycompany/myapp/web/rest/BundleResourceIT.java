package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Bundle;
import com.mycompany.myapp.repository.BundleRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BundleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BundleResourceIT {

    private static final String DEFAULT_BUNDLE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_BUNDLE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PRICE_RANGE = "AAAAAAAAAA";
    private static final String UPDATED_PRICE_RANGE = "BBBBBBBBBB";

    private static final String DEFAULT_MAX_PEOPLE = "AAAAAAAAAA";
    private static final String UPDATED_MAX_PEOPLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/bundles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BundleRepository bundleRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBundleMockMvc;

    private Bundle bundle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bundle createEntity(EntityManager em) {
        Bundle bundle = new Bundle().bundleName(DEFAULT_BUNDLE_NAME).priceRange(DEFAULT_PRICE_RANGE).maxPeople(DEFAULT_MAX_PEOPLE);
        return bundle;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bundle createUpdatedEntity(EntityManager em) {
        Bundle bundle = new Bundle().bundleName(UPDATED_BUNDLE_NAME).priceRange(UPDATED_PRICE_RANGE).maxPeople(UPDATED_MAX_PEOPLE);
        return bundle;
    }

    @BeforeEach
    public void initTest() {
        bundle = createEntity(em);
    }

    @Test
    @Transactional
    void createBundle() throws Exception {
        int databaseSizeBeforeCreate = bundleRepository.findAll().size();
        // Create the Bundle
        restBundleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bundle)))
            .andExpect(status().isCreated());

        // Validate the Bundle in the database
        List<Bundle> bundleList = bundleRepository.findAll();
        assertThat(bundleList).hasSize(databaseSizeBeforeCreate + 1);
        Bundle testBundle = bundleList.get(bundleList.size() - 1);
        assertThat(testBundle.getBundleName()).isEqualTo(DEFAULT_BUNDLE_NAME);
        assertThat(testBundle.getPriceRange()).isEqualTo(DEFAULT_PRICE_RANGE);
        assertThat(testBundle.getMaxPeople()).isEqualTo(DEFAULT_MAX_PEOPLE);
    }

    @Test
    @Transactional
    void createBundleWithExistingId() throws Exception {
        // Create the Bundle with an existing ID
        bundle.setId(1L);

        int databaseSizeBeforeCreate = bundleRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBundleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bundle)))
            .andExpect(status().isBadRequest());

        // Validate the Bundle in the database
        List<Bundle> bundleList = bundleRepository.findAll();
        assertThat(bundleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBundles() throws Exception {
        // Initialize the database
        bundleRepository.saveAndFlush(bundle);

        // Get all the bundleList
        restBundleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bundle.getId().intValue())))
            .andExpect(jsonPath("$.[*].bundleName").value(hasItem(DEFAULT_BUNDLE_NAME)))
            .andExpect(jsonPath("$.[*].priceRange").value(hasItem(DEFAULT_PRICE_RANGE)))
            .andExpect(jsonPath("$.[*].maxPeople").value(hasItem(DEFAULT_MAX_PEOPLE)));
    }

    @Test
    @Transactional
    void getBundle() throws Exception {
        // Initialize the database
        bundleRepository.saveAndFlush(bundle);

        // Get the bundle
        restBundleMockMvc
            .perform(get(ENTITY_API_URL_ID, bundle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bundle.getId().intValue()))
            .andExpect(jsonPath("$.bundleName").value(DEFAULT_BUNDLE_NAME))
            .andExpect(jsonPath("$.priceRange").value(DEFAULT_PRICE_RANGE))
            .andExpect(jsonPath("$.maxPeople").value(DEFAULT_MAX_PEOPLE));
    }

    @Test
    @Transactional
    void getNonExistingBundle() throws Exception {
        // Get the bundle
        restBundleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBundle() throws Exception {
        // Initialize the database
        bundleRepository.saveAndFlush(bundle);

        int databaseSizeBeforeUpdate = bundleRepository.findAll().size();

        // Update the bundle
        Bundle updatedBundle = bundleRepository.findById(bundle.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBundle are not directly saved in db
        em.detach(updatedBundle);
        updatedBundle.bundleName(UPDATED_BUNDLE_NAME).priceRange(UPDATED_PRICE_RANGE).maxPeople(UPDATED_MAX_PEOPLE);

        restBundleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBundle.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBundle))
            )
            .andExpect(status().isOk());

        // Validate the Bundle in the database
        List<Bundle> bundleList = bundleRepository.findAll();
        assertThat(bundleList).hasSize(databaseSizeBeforeUpdate);
        Bundle testBundle = bundleList.get(bundleList.size() - 1);
        assertThat(testBundle.getBundleName()).isEqualTo(UPDATED_BUNDLE_NAME);
        assertThat(testBundle.getPriceRange()).isEqualTo(UPDATED_PRICE_RANGE);
        assertThat(testBundle.getMaxPeople()).isEqualTo(UPDATED_MAX_PEOPLE);
    }

    @Test
    @Transactional
    void putNonExistingBundle() throws Exception {
        int databaseSizeBeforeUpdate = bundleRepository.findAll().size();
        bundle.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBundleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bundle.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bundle))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bundle in the database
        List<Bundle> bundleList = bundleRepository.findAll();
        assertThat(bundleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBundle() throws Exception {
        int databaseSizeBeforeUpdate = bundleRepository.findAll().size();
        bundle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBundleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bundle))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bundle in the database
        List<Bundle> bundleList = bundleRepository.findAll();
        assertThat(bundleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBundle() throws Exception {
        int databaseSizeBeforeUpdate = bundleRepository.findAll().size();
        bundle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBundleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bundle)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bundle in the database
        List<Bundle> bundleList = bundleRepository.findAll();
        assertThat(bundleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBundleWithPatch() throws Exception {
        // Initialize the database
        bundleRepository.saveAndFlush(bundle);

        int databaseSizeBeforeUpdate = bundleRepository.findAll().size();

        // Update the bundle using partial update
        Bundle partialUpdatedBundle = new Bundle();
        partialUpdatedBundle.setId(bundle.getId());

        partialUpdatedBundle.bundleName(UPDATED_BUNDLE_NAME).maxPeople(UPDATED_MAX_PEOPLE);

        restBundleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBundle.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBundle))
            )
            .andExpect(status().isOk());

        // Validate the Bundle in the database
        List<Bundle> bundleList = bundleRepository.findAll();
        assertThat(bundleList).hasSize(databaseSizeBeforeUpdate);
        Bundle testBundle = bundleList.get(bundleList.size() - 1);
        assertThat(testBundle.getBundleName()).isEqualTo(UPDATED_BUNDLE_NAME);
        assertThat(testBundle.getPriceRange()).isEqualTo(DEFAULT_PRICE_RANGE);
        assertThat(testBundle.getMaxPeople()).isEqualTo(UPDATED_MAX_PEOPLE);
    }

    @Test
    @Transactional
    void fullUpdateBundleWithPatch() throws Exception {
        // Initialize the database
        bundleRepository.saveAndFlush(bundle);

        int databaseSizeBeforeUpdate = bundleRepository.findAll().size();

        // Update the bundle using partial update
        Bundle partialUpdatedBundle = new Bundle();
        partialUpdatedBundle.setId(bundle.getId());

        partialUpdatedBundle.bundleName(UPDATED_BUNDLE_NAME).priceRange(UPDATED_PRICE_RANGE).maxPeople(UPDATED_MAX_PEOPLE);

        restBundleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBundle.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBundle))
            )
            .andExpect(status().isOk());

        // Validate the Bundle in the database
        List<Bundle> bundleList = bundleRepository.findAll();
        assertThat(bundleList).hasSize(databaseSizeBeforeUpdate);
        Bundle testBundle = bundleList.get(bundleList.size() - 1);
        assertThat(testBundle.getBundleName()).isEqualTo(UPDATED_BUNDLE_NAME);
        assertThat(testBundle.getPriceRange()).isEqualTo(UPDATED_PRICE_RANGE);
        assertThat(testBundle.getMaxPeople()).isEqualTo(UPDATED_MAX_PEOPLE);
    }

    @Test
    @Transactional
    void patchNonExistingBundle() throws Exception {
        int databaseSizeBeforeUpdate = bundleRepository.findAll().size();
        bundle.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBundleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bundle.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bundle))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bundle in the database
        List<Bundle> bundleList = bundleRepository.findAll();
        assertThat(bundleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBundle() throws Exception {
        int databaseSizeBeforeUpdate = bundleRepository.findAll().size();
        bundle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBundleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bundle))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bundle in the database
        List<Bundle> bundleList = bundleRepository.findAll();
        assertThat(bundleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBundle() throws Exception {
        int databaseSizeBeforeUpdate = bundleRepository.findAll().size();
        bundle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBundleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(bundle)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bundle in the database
        List<Bundle> bundleList = bundleRepository.findAll();
        assertThat(bundleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBundle() throws Exception {
        // Initialize the database
        bundleRepository.saveAndFlush(bundle);

        int databaseSizeBeforeDelete = bundleRepository.findAll().size();

        // Delete the bundle
        restBundleMockMvc
            .perform(delete(ENTITY_API_URL_ID, bundle.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Bundle> bundleList = bundleRepository.findAll();
        assertThat(bundleList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
