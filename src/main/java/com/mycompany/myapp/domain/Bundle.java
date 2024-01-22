package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * A Bundle.
 */
@Entity
@Table(name = "bundle")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Bundle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "bundle_name")
    private String bundleName;

    @Column(name = "price_range")
    private String priceRange;

    @Column(name = "max_people")
    private String maxPeople;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Bundle id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBundleName() {
        return this.bundleName;
    }

    public Bundle bundleName(String bundleName) {
        this.setBundleName(bundleName);
        return this;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    public String getPriceRange() {
        return this.priceRange;
    }

    public Bundle priceRange(String priceRange) {
        this.setPriceRange(priceRange);
        return this;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getMaxPeople() {
        return this.maxPeople;
    }

    public Bundle maxPeople(String maxPeople) {
        this.setMaxPeople(maxPeople);
        return this;
    }

    public void setMaxPeople(String maxPeople) {
        this.maxPeople = maxPeople;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bundle)) {
            return false;
        }
        return getId() != null && getId().equals(((Bundle) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Bundle{" +
            "id=" + getId() +
            ", bundleName='" + getBundleName() + "'" +
            ", priceRange='" + getPriceRange() + "'" +
            ", maxPeople='" + getMaxPeople() + "'" +
            "}";
    }
}
