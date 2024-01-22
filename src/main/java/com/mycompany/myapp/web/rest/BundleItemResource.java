package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.BundleItem;
import com.mycompany.myapp.repository.BundleItemRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.BundleItem}.
 */
@RestController
@RequestMapping("/api/bundle-items")
@Transactional
public class BundleItemResource {

    private final Logger log = LoggerFactory.getLogger(BundleItemResource.class);

    private static final String ENTITY_NAME = "bundleItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BundleItemRepository bundleItemRepository;

    public BundleItemResource(BundleItemRepository bundleItemRepository) {
        this.bundleItemRepository = bundleItemRepository;
    }

    /**
     * {@code POST  /bundle-items} : Create a new bundleItem.
     *
     * @param bundleItem the bundleItem to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bundleItem, or with status {@code 400 (Bad Request)} if the bundleItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BundleItem> createBundleItem(@RequestBody BundleItem bundleItem) throws URISyntaxException {
        log.debug("REST request to save BundleItem : {}", bundleItem);
        if (bundleItem.getId() != null) {
            throw new BadRequestAlertException("A new bundleItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BundleItem result = bundleItemRepository.save(bundleItem);
        return ResponseEntity
            .created(new URI("/api/bundle-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /bundle-items/:id} : Updates an existing bundleItem.
     *
     * @param id the id of the bundleItem to save.
     * @param bundleItem the bundleItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bundleItem,
     * or with status {@code 400 (Bad Request)} if the bundleItem is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bundleItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BundleItem> updateBundleItem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BundleItem bundleItem
    ) throws URISyntaxException {
        log.debug("REST request to update BundleItem : {}, {}", id, bundleItem);
        if (bundleItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bundleItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bundleItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BundleItem result = bundleItemRepository.save(bundleItem);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bundleItem.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /bundle-items/:id} : Partial updates given fields of an existing bundleItem, field will ignore if it is null
     *
     * @param id the id of the bundleItem to save.
     * @param bundleItem the bundleItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bundleItem,
     * or with status {@code 400 (Bad Request)} if the bundleItem is not valid,
     * or with status {@code 404 (Not Found)} if the bundleItem is not found,
     * or with status {@code 500 (Internal Server Error)} if the bundleItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BundleItem> partialUpdateBundleItem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BundleItem bundleItem
    ) throws URISyntaxException {
        log.debug("REST request to partial update BundleItem partially : {}, {}", id, bundleItem);
        if (bundleItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bundleItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bundleItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BundleItem> result = bundleItemRepository
            .findById(bundleItem.getId())
            .map(existingBundleItem -> {
                if (bundleItem.getBundleId() != null) {
                    existingBundleItem.setBundleId(bundleItem.getBundleId());
                }
                if (bundleItem.getItemId() != null) {
                    existingBundleItem.setItemId(bundleItem.getItemId());
                }

                return existingBundleItem;
            })
            .map(bundleItemRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bundleItem.getId().toString())
        );
    }

    /**
     * {@code GET  /bundle-items} : get all the bundleItems.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bundleItems in body.
     */
    @GetMapping("")
    public List<BundleItem> getAllBundleItems() {
        log.debug("REST request to get all BundleItems");
        return bundleItemRepository.findAll();
    }

    /**
     * {@code GET  /bundle-items/:id} : get the "id" bundleItem.
     *
     * @param id the id of the bundleItem to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bundleItem, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BundleItem> getBundleItem(@PathVariable("id") Long id) {
        log.debug("REST request to get BundleItem : {}", id);
        Optional<BundleItem> bundleItem = bundleItemRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(bundleItem);
    }

    /**
     * {@code DELETE  /bundle-items/:id} : delete the "id" bundleItem.
     *
     * @param id the id of the bundleItem to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBundleItem(@PathVariable("id") Long id) {
        log.debug("REST request to delete BundleItem : {}", id);
        bundleItemRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
