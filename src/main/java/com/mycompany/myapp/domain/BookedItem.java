package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A BookedItem.
 */
@Entity
@Table(name = "booked_item")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookedItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "booking_id")
    private Integer bookingId;

    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "custom_item_name")
    private String customItemName;

    @Column(name = "available_date")
    private LocalDate availableDate;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BookedItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBookingId() {
        return this.bookingId;
    }

    public BookedItem bookingId(Integer bookingId) {
        this.setBookingId(bookingId);
        return this;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getItemId() {
        return this.itemId;
    }

    public BookedItem itemId(Integer itemId) {
        this.setItemId(itemId);
        return this;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getCustomItemName() {
        return this.customItemName;
    }

    public BookedItem customItemName(String customItemName) {
        this.setCustomItemName(customItemName);
        return this;
    }

    public void setCustomItemName(String customItemName) {
        this.customItemName = customItemName;
    }

    public LocalDate getAvailableDate() {
        return this.availableDate;
    }

    public BookedItem availableDate(LocalDate availableDate) {
        this.setAvailableDate(availableDate);
        return this;
    }

    public void setAvailableDate(LocalDate availableDate) {
        this.availableDate = availableDate;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookedItem)) {
            return false;
        }
        return getId() != null && getId().equals(((BookedItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookedItem{" +
            "id=" + getId() +
            ", bookingId=" + getBookingId() +
            ", itemId=" + getItemId() +
            ", customItemName='" + getCustomItemName() + "'" +
            ", availableDate='" + getAvailableDate() + "'" +
            "}";
    }
}
