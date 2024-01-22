package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.BundleItem;
import com.mycompany.myapp.repository.BundleItemRepository;
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
 * Integration tests for the {@link BundleItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BundleItemResourceIT {

    private static final Integer DEFAULT_BUNDLE_ID = 1;
    private static final Integer UPDATED_BUNDLE_ID = 2;

    private static final Integer DEFAULT_ITEM_ID = 1;
    private static final Integer UPDATED_ITEM_ID = 2;

    private static final String ENTITY_API_URL = "/api/bundle-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BundleItemRepository bundleItemRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBundleItemMockMvc;

    private BundleItem bundleItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BundleItem createEntity(EntityManager em) {
        BundleItem bundleItem = new BundleItem().bundleId(DEFAULT_BUNDLE_ID).itemId(DEFAULT_ITEM_ID);
        return bundleItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BundleItem createUpdatedEntity(EntityManager em) {
        BundleItem bundleItem = new BundleItem().bundleId(UPDATED_BUNDLE_ID).itemId(UPDATED_ITEM_ID);
        return bundleItem;
    }

    @BeforeEach
    public void initTest() {
        bundleItem = createEntity(em);
    }

    @Test
    @Transactional
    void createBundleItem() throws Exception {
        int databaseSizeBeforeCreate = bundleItemRepository.findAll().size();
        // Create the BundleItem
        restBundleItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bundleItem)))
            .andExpect(status().isCreated());

        // Validate the BundleItem in the database
        List<BundleItem> bundleItemList = bundleItemRepository.findAll();
        assertThat(bundleItemList).hasSize(databaseSizeBeforeCreate + 1);
        BundleItem testBundleItem = bundleItemList.get(bundleItemList.size() - 1);
        assertThat(testBundleItem.getBundleId()).isEqualTo(DEFAULT_BUNDLE_ID);
        assertThat(testBundleItem.getItemId()).isEqualTo(DEFAULT_ITEM_ID);
    }

    @Test
    @Transactional
    void createBundleItemWithExistingId() throws Exception {
        // Create the BundleItem with an existing ID
        bundleItem.setId(1L);

        int databaseSizeBeforeCreate = bundleItemRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBundleItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bundleItem)))
            .andExpect(status().isBadRequest());

        // Validate the BundleItem in the database
        List<BundleItem> bundleItemList = bundleItemRepository.findAll();
        assertThat(bundleItemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBundleItems() throws Exception {
        // Initialize the database
        bundleItemRepository.saveAndFlush(bundleItem);

        // Get all the bundleItemList
        restBundleItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bundleItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].bundleId").value(hasItem(DEFAULT_BUNDLE_ID)))
            .andExpect(jsonPath("$.[*].itemId").value(hasItem(DEFAULT_ITEM_ID)));
    }

    @Test
    @Transactional
    void getBundleItem() throws Exception {
        // Initialize the database
        bundleItemRepository.saveAndFlush(bundleItem);

        // Get the bundleItem
        restBundleItemMockMvc
            .perform(get(ENTITY_API_URL_ID, bundleItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bundleItem.getId().intValue()))
            .andExpect(jsonPath("$.bundleId").value(DEFAULT_BUNDLE_ID))
            .andExpect(jsonPath("$.itemId").value(DEFAULT_ITEM_ID));
    }

    @Test
    @Transactional
    void getNonExistingBundleItem() throws Exception {
        // Get the bundleItem
        restBundleItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBundleItem() throws Exception {
        // Initialize the database
        bundleItemRepository.saveAndFlush(bundleItem);

        int databaseSizeBeforeUpdate = bundleItemRepository.findAll().size();

        // Update the bundleItem
        BundleItem updatedBundleItem = bundleItemRepository.findById(bundleItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBundleItem are not directly saved in db
        em.detach(updatedBundleItem);
        updatedBundleItem.bundleId(UPDATED_BUNDLE_ID).itemId(UPDATED_ITEM_ID);

        restBundleItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBundleItem.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBundleItem))
            )
            .andExpect(status().isOk());

        // Validate the BundleItem in the database
        List<BundleItem> bundleItemList = bundleItemRepository.findAll();
        assertThat(bundleItemList).hasSize(databaseSizeBeforeUpdate);
        BundleItem testBundleItem = bundleItemList.get(bundleItemList.size() - 1);
        assertThat(testBundleItem.getBundleId()).isEqualTo(UPDATED_BUNDLE_ID);
        assertThat(testBundleItem.getItemId()).isEqualTo(UPDATED_ITEM_ID);
    }

    @Test
    @Transactional
    void putNonExistingBundleItem() throws Exception {
        int databaseSizeBeforeUpdate = bundleItemRepository.findAll().size();
        bundleItem.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBundleItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bundleItem.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bundleItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the BundleItem in the database
        List<BundleItem> bundleItemList = bundleItemRepository.findAll();
        assertThat(bundleItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBundleItem() throws Exception {
        int databaseSizeBeforeUpdate = bundleItemRepository.findAll().size();
        bundleItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBundleItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bundleItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the BundleItem in the database
        List<BundleItem> bundleItemList = bundleItemRepository.findAll();
        assertThat(bundleItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBundleItem() throws Exception {
        int databaseSizeBeforeUpdate = bundleItemRepository.findAll().size();
        bundleItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBundleItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bundleItem)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BundleItem in the database
        List<BundleItem> bundleItemList = bundleItemRepository.findAll();
        assertThat(bundleItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBundleItemWithPatch() throws Exception {
        // Initialize the database
        bundleItemRepository.saveAndFlush(bundleItem);

        int databaseSizeBeforeUpdate = bundleItemRepository.findAll().size();

        // Update the bundleItem using partial update
        BundleItem partialUpdatedBundleItem = new BundleItem();
        partialUpdatedBundleItem.setId(bundleItem.getId());

        partialUpdatedBundleItem.itemId(UPDATED_ITEM_ID);

        restBundleItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBundleItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBundleItem))
            )
            .andExpect(status().isOk());

        // Validate the BundleItem in the database
        List<BundleItem> bundleItemList = bundleItemRepository.findAll();
        assertThat(bundleItemList).hasSize(databaseSizeBeforeUpdate);
        BundleItem testBundleItem = bundleItemList.get(bundleItemList.size() - 1);
        assertThat(testBundleItem.getBundleId()).isEqualTo(DEFAULT_BUNDLE_ID);
        assertThat(testBundleItem.getItemId()).isEqualTo(UPDATED_ITEM_ID);
    }

    @Test
    @Transactional
    void fullUpdateBundleItemWithPatch() throws Exception {
        // Initialize the database
        bundleItemRepository.saveAndFlush(bundleItem);

        int databaseSizeBeforeUpdate = bundleItemRepository.findAll().size();

        // Update the bundleItem using partial update
        BundleItem partialUpdatedBundleItem = new BundleItem();
        partialUpdatedBundleItem.setId(bundleItem.getId());

        partialUpdatedBundleItem.bundleId(UPDATED_BUNDLE_ID).itemId(UPDATED_ITEM_ID);

        restBundleItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBundleItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBundleItem))
            )
            .andExpect(status().isOk());

        // Validate the BundleItem in the database
        List<BundleItem> bundleItemList = bundleItemRepository.findAll();
        assertThat(bundleItemList).hasSize(databaseSizeBeforeUpdate);
        BundleItem testBundleItem = bundleItemList.get(bundleItemList.size() - 1);
        assertThat(testBundleItem.getBundleId()).isEqualTo(UPDATED_BUNDLE_ID);
        assertThat(testBundleItem.getItemId()).isEqualTo(UPDATED_ITEM_ID);
    }

    @Test
    @Transactional
    void patchNonExistingBundleItem() throws Exception {
        int databaseSizeBeforeUpdate = bundleItemRepository.findAll().size();
        bundleItem.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBundleItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bundleItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bundleItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the BundleItem in the database
        List<BundleItem> bundleItemList = bundleItemRepository.findAll();
        assertThat(bundleItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBundleItem() throws Exception {
        int databaseSizeBeforeUpdate = bundleItemRepository.findAll().size();
        bundleItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBundleItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bundleItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the BundleItem in the database
        List<BundleItem> bundleItemList = bundleItemRepository.findAll();
        assertThat(bundleItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBundleItem() throws Exception {
        int databaseSizeBeforeUpdate = bundleItemRepository.findAll().size();
        bundleItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBundleItemMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(bundleItem))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BundleItem in the database
        List<BundleItem> bundleItemList = bundleItemRepository.findAll();
        assertThat(bundleItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBundleItem() throws Exception {
        // Initialize the database
        bundleItemRepository.saveAndFlush(bundleItem);

        int databaseSizeBeforeDelete = bundleItemRepository.findAll().size();

        // Delete the bundleItem
        restBundleItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, bundleItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BundleItem> bundleItemList = bundleItemRepository.findAll();
        assertThat(bundleItemList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
