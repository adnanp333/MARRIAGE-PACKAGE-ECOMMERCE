package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Bundle;
import com.mycompany.myapp.domain.Item;
import com.mycompany.myapp.repository.BundleItemRepository;
import com.mycompany.myapp.repository.BundleRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Bundle}.
 */
@RestController
@RequestMapping("/api/bundles")
@Transactional
public class BundleResource {

    private final Logger log = LoggerFactory.getLogger(BundleResource.class);

    private static final String ENTITY_NAME = "bundle";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BundleRepository bundleRepository;
    private final BundleItemRepository bundleItemRepository;

    public BundleResource(BundleRepository bundleRepository, BundleItemRepository bundleItemRepository) {
        this.bundleRepository = bundleRepository;
        this.bundleItemRepository = bundleItemRepository;
    }

    /**
     * {@code POST  /bundles} : Create a new bundle.
     *
     * @param bundle the bundle to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new bundle, or with status {@code 400 (Bad Request)} if the
     *         bundle has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Bundle> createBundle(@RequestBody Bundle bundle) throws URISyntaxException {
        log.debug("REST request to save Bundle : {}", bundle);
        if (bundle.getId() != null) {
            throw new BadRequestAlertException("A new bundle cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Bundle result = bundleRepository.save(bundle);
        return ResponseEntity
                .created(new URI("/api/bundles/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME,
                        result.getId().toString()))
                .body(result);
    }

    /**
     * {@code PUT  /bundles/:id} : Updates an existing bundle.
     *
     * @param id     the id of the bundle to save.
     * @param bundle the bundle to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated bundle,
     *         or with status {@code 400 (Bad Request)} if the bundle is not valid,
     *         or with status {@code 500 (Internal Server Error)} if the bundle
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Bundle> updateBundle(@PathVariable(value = "id", required = false) final Long id,
            @RequestBody Bundle bundle)
            throws URISyntaxException {
        log.debug("REST request to update Bundle : {}, {}", id, bundle);
        if (bundle.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bundle.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bundleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Bundle result = bundleRepository.save(bundle);
        return ResponseEntity
                .ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME,
                        bundle.getId().toString()))
                .body(result);
    }

    /**
     * {@code PATCH  /bundles/:id} : Partial updates given fields of an existing
     * bundle, field will ignore if it is null
     *
     * @param id     the id of the bundle to save.
     * @param bundle the bundle to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated bundle,
     *         or with status {@code 400 (Bad Request)} if the bundle is not valid,
     *         or with status {@code 404 (Not Found)} if the bundle is not found,
     *         or with status {@code 500 (Internal Server Error)} if the bundle
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Bundle> partialUpdateBundle(
            @PathVariable(value = "id", required = false) final Long id,
            @RequestBody Bundle bundle) throws URISyntaxException {
        log.debug("REST request to partial update Bundle partially : {}, {}", id, bundle);
        if (bundle.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bundle.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bundleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Bundle> result = bundleRepository
                .findById(bundle.getId())
                .map(existingBundle -> {
                    if (bundle.getBundleName() != null) {
                        existingBundle.setBundleName(bundle.getBundleName());
                    }
                    if (bundle.getPriceRange() != null) {
                        existingBundle.setPriceRange(bundle.getPriceRange());
                    }
                    if (bundle.getMaxPeople() != null) {
                        existingBundle.setMaxPeople(bundle.getMaxPeople());
                    }

                    return existingBundle;
                })
                .map(bundleRepository::save);

        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bundle.getId().toString()));
    }

    /**
     * {@code GET  /bundles} : get all the bundles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of bundles in body.
     */
    @GetMapping("")
    public List<Bundle> getAllBundles() {
        log.debug("REST request to get all Bundles");
        return bundleRepository.findAll();
    }

    /**
     * {@code GET  /bundles/:id} : get the "id" bundle.
     *
     * @param id the id of the bundle to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the bundle, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Bundle> getBundle(@PathVariable("id") Long id) {
        log.debug("REST request to get Bundle : {}", id);
        Optional<Bundle> bundle = bundleRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(bundle);
    }

    /**
     * {@code DELETE  /bundles/:id} : delete the "id" bundle.
     *
     * @param id the id of the bundle to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBundle(@PathVariable("id") Long id) {
        log.debug("REST request to delete Bundle : {}", id);
        bundleRepository.deleteById(id);
        return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                .build();
    }

    @GetMapping("/{id}/items")
    public List<Item> getAllBundleItems(@PathVariable Integer id) {
        log.debug("REST request to get all Bundles");
        return bundleItemRepository.findAllBundleItems(id);
    }
}
