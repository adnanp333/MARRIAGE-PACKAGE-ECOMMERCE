package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.BookedItem;
import com.mycompany.myapp.repository.BookedItemRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.BookedItem}.
 */
@RestController
@RequestMapping("/api/booked-items")
@Transactional
public class BookedItemResource {

    private final Logger log = LoggerFactory.getLogger(BookedItemResource.class);

    private static final String ENTITY_NAME = "bookedItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BookedItemRepository bookedItemRepository;

    public BookedItemResource(BookedItemRepository bookedItemRepository) {
        this.bookedItemRepository = bookedItemRepository;
    }

    /**
     * {@code POST  /booked-items} : Create a new bookedItem.
     *
     * @param bookedItem the bookedItem to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bookedItem, or with status {@code 400 (Bad Request)} if the bookedItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BookedItem> createBookedItem(@RequestBody BookedItem bookedItem) throws URISyntaxException {
        log.debug("REST request to save BookedItem : {}", bookedItem);
        if (bookedItem.getId() != null) {
            throw new BadRequestAlertException("A new bookedItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BookedItem result = bookedItemRepository.save(bookedItem);
        return ResponseEntity
            .created(new URI("/api/booked-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /booked-items/:id} : Updates an existing bookedItem.
     *
     * @param id the id of the bookedItem to save.
     * @param bookedItem the bookedItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookedItem,
     * or with status {@code 400 (Bad Request)} if the bookedItem is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bookedItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookedItem> updateBookedItem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BookedItem bookedItem
    ) throws URISyntaxException {
        log.debug("REST request to update BookedItem : {}, {}", id, bookedItem);
        if (bookedItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookedItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookedItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BookedItem result = bookedItemRepository.save(bookedItem);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bookedItem.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /booked-items/:id} : Partial updates given fields of an existing bookedItem, field will ignore if it is null
     *
     * @param id the id of the bookedItem to save.
     * @param bookedItem the bookedItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookedItem,
     * or with status {@code 400 (Bad Request)} if the bookedItem is not valid,
     * or with status {@code 404 (Not Found)} if the bookedItem is not found,
     * or with status {@code 500 (Internal Server Error)} if the bookedItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BookedItem> partialUpdateBookedItem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BookedItem bookedItem
    ) throws URISyntaxException {
        log.debug("REST request to partial update BookedItem partially : {}, {}", id, bookedItem);
        if (bookedItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookedItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookedItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BookedItem> result = bookedItemRepository
            .findById(bookedItem.getId())
            .map(existingBookedItem -> {
                if (bookedItem.getBookingId() != null) {
                    existingBookedItem.setBookingId(bookedItem.getBookingId());
                }
                if (bookedItem.getItemId() != null) {
                    existingBookedItem.setItemId(bookedItem.getItemId());
                }
                if (bookedItem.getCustomItemName() != null) {
                    existingBookedItem.setCustomItemName(bookedItem.getCustomItemName());
                }
                if (bookedItem.getAvailableDate() != null) {
                    existingBookedItem.setAvailableDate(bookedItem.getAvailableDate());
                }

                return existingBookedItem;
            })
            .map(bookedItemRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bookedItem.getId().toString())
        );
    }

    /**
     * {@code GET  /booked-items} : get all the bookedItems.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bookedItems in body.
     */
    @GetMapping("")
    public List<BookedItem> getAllBookedItems() {
        log.debug("REST request to get all BookedItems");
        return bookedItemRepository.findAll();
    }

    /**
     * {@code GET  /booked-items/:id} : get the "id" bookedItem.
     *
     * @param id the id of the bookedItem to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bookedItem, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookedItem> getBookedItem(@PathVariable("id") Long id) {
        log.debug("REST request to get BookedItem : {}", id);
        Optional<BookedItem> bookedItem = bookedItemRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(bookedItem);
    }

    /**
     * {@code DELETE  /booked-items/:id} : delete the "id" bookedItem.
     *
     * @param id the id of the bookedItem to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookedItem(@PathVariable("id") Long id) {
        log.debug("REST request to delete BookedItem : {}", id);
        bookedItemRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
