package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * A BundleItem.
 */
@Entity
@Table(name = "bundle_item")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BundleItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "bundle_id")
    private Integer bundleId;

    @Column(name = "item_id")
    private Integer itemId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BundleItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBundleId() {
        return this.bundleId;
    }

    public BundleItem bundleId(Integer bundleId) {
        this.setBundleId(bundleId);
        return this;
    }

    public void setBundleId(Integer bundleId) {
        this.bundleId = bundleId;
    }

    public Integer getItemId() {
        return this.itemId;
    }

    public BundleItem itemId(Integer itemId) {
        this.setItemId(itemId);
        return this;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BundleItem)) {
            return false;
        }
        return getId() != null && getId().equals(((BundleItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BundleItem{" +
            "id=" + getId() +
            ", bundleId=" + getBundleId() +
            ", itemId=" + getItemId() +
            "}";
    }
}
