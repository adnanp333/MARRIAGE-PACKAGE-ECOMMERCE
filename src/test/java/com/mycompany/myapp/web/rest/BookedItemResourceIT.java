package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.BookedItem;
import com.mycompany.myapp.repository.BookedItemRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link BookedItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BookedItemResourceIT {

    private static final Integer DEFAULT_BOOKING_ID = 1;
    private static final Integer UPDATED_BOOKING_ID = 2;

    private static final Integer DEFAULT_ITEM_ID = 1;
    private static final Integer UPDATED_ITEM_ID = 2;

    private static final String DEFAULT_CUSTOM_ITEM_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CUSTOM_ITEM_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_AVAILABLE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_AVAILABLE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/booked-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BookedItemRepository bookedItemRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookedItemMockMvc;

    private BookedItem bookedItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookedItem createEntity(EntityManager em) {
        BookedItem bookedItem = new BookedItem()
            .bookingId(DEFAULT_BOOKING_ID)
            .itemId(DEFAULT_ITEM_ID)
            .customItemName(DEFAULT_CUSTOM_ITEM_NAME)
            .availableDate(DEFAULT_AVAILABLE_DATE);
        return bookedItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookedItem createUpdatedEntity(EntityManager em) {
        BookedItem bookedItem = new BookedItem()
            .bookingId(UPDATED_BOOKING_ID)
            .itemId(UPDATED_ITEM_ID)
            .customItemName(UPDATED_CUSTOM_ITEM_NAME)
            .availableDate(UPDATED_AVAILABLE_DATE);
        return bookedItem;
    }

    @BeforeEach
    public void initTest() {
        bookedItem = createEntity(em);
    }

    @Test
    @Transactional
    void createBookedItem() throws Exception {
        int databaseSizeBeforeCreate = bookedItemRepository.findAll().size();
        // Create the BookedItem
        restBookedItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bookedItem)))
            .andExpect(status().isCreated());

        // Validate the BookedItem in the database
        List<BookedItem> bookedItemList = bookedItemRepository.findAll();
        assertThat(bookedItemList).hasSize(databaseSizeBeforeCreate + 1);
        BookedItem testBookedItem = bookedItemList.get(bookedItemList.size() - 1);
        assertThat(testBookedItem.getBookingId()).isEqualTo(DEFAULT_BOOKING_ID);
        assertThat(testBookedItem.getItemId()).isEqualTo(DEFAULT_ITEM_ID);
        assertThat(testBookedItem.getCustomItemName()).isEqualTo(DEFAULT_CUSTOM_ITEM_NAME);
        assertThat(testBookedItem.getAvailableDate()).isEqualTo(DEFAULT_AVAILABLE_DATE);
    }

    @Test
    @Transactional
    void createBookedItemWithExistingId() throws Exception {
        // Create the BookedItem with an existing ID
        bookedItem.setId(1L);

        int databaseSizeBeforeCreate = bookedItemRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookedItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bookedItem)))
            .andExpect(status().isBadRequest());

        // Validate the BookedItem in the database
        List<BookedItem> bookedItemList = bookedItemRepository.findAll();
        assertThat(bookedItemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBookedItems() throws Exception {
        // Initialize the database
        bookedItemRepository.saveAndFlush(bookedItem);

        // Get all the bookedItemList
        restBookedItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookedItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].bookingId").value(hasItem(DEFAULT_BOOKING_ID)))
            .andExpect(jsonPath("$.[*].itemId").value(hasItem(DEFAULT_ITEM_ID)))
            .andExpect(jsonPath("$.[*].customItemName").value(hasItem(DEFAULT_CUSTOM_ITEM_NAME)))
            .andExpect(jsonPath("$.[*].availableDate").value(hasItem(DEFAULT_AVAILABLE_DATE.toString())));
    }

    @Test
    @Transactional
    void getBookedItem() throws Exception {
        // Initialize the database
        bookedItemRepository.saveAndFlush(bookedItem);

        // Get the bookedItem
        restBookedItemMockMvc
            .perform(get(ENTITY_API_URL_ID, bookedItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bookedItem.getId().intValue()))
            .andExpect(jsonPath("$.bookingId").value(DEFAULT_BOOKING_ID))
            .andExpect(jsonPath("$.itemId").value(DEFAULT_ITEM_ID))
            .andExpect(jsonPath("$.customItemName").value(DEFAULT_CUSTOM_ITEM_NAME))
            .andExpect(jsonPath("$.availableDate").value(DEFAULT_AVAILABLE_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingBookedItem() throws Exception {
        // Get the bookedItem
        restBookedItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBookedItem() throws Exception {
        // Initialize the database
        bookedItemRepository.saveAndFlush(bookedItem);

        int databaseSizeBeforeUpdate = bookedItemRepository.findAll().size();

        // Update the bookedItem
        BookedItem updatedBookedItem = bookedItemRepository.findById(bookedItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBookedItem are not directly saved in db
        em.detach(updatedBookedItem);
        updatedBookedItem
            .bookingId(UPDATED_BOOKING_ID)
            .itemId(UPDATED_ITEM_ID)
            .customItemName(UPDATED_CUSTOM_ITEM_NAME)
            .availableDate(UPDATED_AVAILABLE_DATE);

        restBookedItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBookedItem.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBookedItem))
            )
            .andExpect(status().isOk());

        // Validate the BookedItem in the database
        List<BookedItem> bookedItemList = bookedItemRepository.findAll();
        assertThat(bookedItemList).hasSize(databaseSizeBeforeUpdate);
        BookedItem testBookedItem = bookedItemList.get(bookedItemList.size() - 1);
        assertThat(testBookedItem.getBookingId()).isEqualTo(UPDATED_BOOKING_ID);
        assertThat(testBookedItem.getItemId()).isEqualTo(UPDATED_ITEM_ID);
        assertThat(testBookedItem.getCustomItemName()).isEqualTo(UPDATED_CUSTOM_ITEM_NAME);
        assertThat(testBookedItem.getAvailableDate()).isEqualTo(UPDATED_AVAILABLE_DATE);
    }

    @Test
    @Transactional
    void putNonExistingBookedItem() throws Exception {
        int databaseSizeBeforeUpdate = bookedItemRepository.findAll().size();
        bookedItem.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookedItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookedItem.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bookedItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookedItem in the database
        List<BookedItem> bookedItemList = bookedItemRepository.findAll();
        assertThat(bookedItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBookedItem() throws Exception {
        int databaseSizeBeforeUpdate = bookedItemRepository.findAll().size();
        bookedItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookedItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bookedItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookedItem in the database
        List<BookedItem> bookedItemList = bookedItemRepository.findAll();
        assertThat(bookedItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBookedItem() throws Exception {
        int databaseSizeBeforeUpdate = bookedItemRepository.findAll().size();
        bookedItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookedItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bookedItem)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookedItem in the database
        List<BookedItem> bookedItemList = bookedItemRepository.findAll();
        assertThat(bookedItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookedItemWithPatch() throws Exception {
        // Initialize the database
        bookedItemRepository.saveAndFlush(bookedItem);

        int databaseSizeBeforeUpdate = bookedItemRepository.findAll().size();

        // Update the bookedItem using partial update
        BookedItem partialUpdatedBookedItem = new BookedItem();
        partialUpdatedBookedItem.setId(bookedItem.getId());

        partialUpdatedBookedItem.bookingId(UPDATED_BOOKING_ID);

        restBookedItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookedItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBookedItem))
            )
            .andExpect(status().isOk());

        // Validate the BookedItem in the database
        List<BookedItem> bookedItemList = bookedItemRepository.findAll();
        assertThat(bookedItemList).hasSize(databaseSizeBeforeUpdate);
        BookedItem testBookedItem = bookedItemList.get(bookedItemList.size() - 1);
        assertThat(testBookedItem.getBookingId()).isEqualTo(UPDATED_BOOKING_ID);
        assertThat(testBookedItem.getItemId()).isEqualTo(DEFAULT_ITEM_ID);
        assertThat(testBookedItem.getCustomItemName()).isEqualTo(DEFAULT_CUSTOM_ITEM_NAME);
        assertThat(testBookedItem.getAvailableDate()).isEqualTo(DEFAULT_AVAILABLE_DATE);
    }

    @Test
    @Transactional
    void fullUpdateBookedItemWithPatch() throws Exception {
        // Initialize the database
        bookedItemRepository.saveAndFlush(bookedItem);

        int databaseSizeBeforeUpdate = bookedItemRepository.findAll().size();

        // Update the bookedItem using partial update
        BookedItem partialUpdatedBookedItem = new BookedItem();
        partialUpdatedBookedItem.setId(bookedItem.getId());

        partialUpdatedBookedItem
            .bookingId(UPDATED_BOOKING_ID)
            .itemId(UPDATED_ITEM_ID)
            .customItemName(UPDATED_CUSTOM_ITEM_NAME)
            .availableDate(UPDATED_AVAILABLE_DATE);

        restBookedItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookedItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBookedItem))
            )
            .andExpect(status().isOk());

        // Validate the BookedItem in the database
        List<BookedItem> bookedItemList = bookedItemRepository.findAll();
        assertThat(bookedItemList).hasSize(databaseSizeBeforeUpdate);
        BookedItem testBookedItem = bookedItemList.get(bookedItemList.size() - 1);
        assertThat(testBookedItem.getBookingId()).isEqualTo(UPDATED_BOOKING_ID);
        assertThat(testBookedItem.getItemId()).isEqualTo(UPDATED_ITEM_ID);
        assertThat(testBookedItem.getCustomItemName()).isEqualTo(UPDATED_CUSTOM_ITEM_NAME);
        assertThat(testBookedItem.getAvailableDate()).isEqualTo(UPDATED_AVAILABLE_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingBookedItem() throws Exception {
        int databaseSizeBeforeUpdate = bookedItemRepository.findAll().size();
        bookedItem.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookedItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bookedItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bookedItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookedItem in the database
        List<BookedItem> bookedItemList = bookedItemRepository.findAll();
        assertThat(bookedItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBookedItem() throws Exception {
        int databaseSizeBeforeUpdate = bookedItemRepository.findAll().size();
        bookedItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookedItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bookedItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookedItem in the database
        List<BookedItem> bookedItemList = bookedItemRepository.findAll();
        assertThat(bookedItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBookedItem() throws Exception {
        int databaseSizeBeforeUpdate = bookedItemRepository.findAll().size();
        bookedItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookedItemMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(bookedItem))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookedItem in the database
        List<BookedItem> bookedItemList = bookedItemRepository.findAll();
        assertThat(bookedItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBookedItem() throws Exception {
        // Initialize the database
        bookedItemRepository.saveAndFlush(bookedItem);

        int databaseSizeBeforeDelete = bookedItemRepository.findAll().size();

        // Delete the bookedItem
        restBookedItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, bookedItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BookedItem> bookedItemList = bookedItemRepository.findAll();
        assertThat(bookedItemList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
