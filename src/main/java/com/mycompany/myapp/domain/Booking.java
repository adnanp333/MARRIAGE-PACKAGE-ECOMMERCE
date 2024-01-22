package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * A Booking.
 */
@Entity
@Table(name = "booking")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "bundle_id")
    private Integer bundleId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "booking_start_date")
    private LocalDate bookingStartDate;

    @Column(name = "booking_end_date")
    private LocalDate bookingEndDate;

    @Column(name = "booking_start_time")
    private ZonedDateTime bookingStartTime;

    @Column(name = "booking_end_time")
    private ZonedDateTime bookingEndTime;

    @Column(name = "user_id")
    private Long userId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Booking id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBundleId() {
        return this.bundleId;
    }

    public Booking bundleId(Integer bundleId) {
        this.setBundleId(bundleId);
        return this;
    }

    public void setBundleId(Integer bundleId) {
        this.bundleId = bundleId;
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public Booking customerName(String customerName) {
        this.setCustomerName(customerName);
        return this;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDate getBookingStartDate() {
        return this.bookingStartDate;
    }

    public Booking bookingStartDate(LocalDate bookingStartDate) {
        this.setBookingStartDate(bookingStartDate);
        return this;
    }

    public void setBookingStartDate(LocalDate bookingStartDate) {
        this.bookingStartDate = bookingStartDate;
    }

    public LocalDate getBookingEndDate() {
        return this.bookingEndDate;
    }

    public Booking bookingEndDate(LocalDate bookingEndDate) {
        this.setBookingEndDate(bookingEndDate);
        return this;
    }

    public void setBookingEndDate(LocalDate bookingEndDate) {
        this.bookingEndDate = bookingEndDate;
    }

    public ZonedDateTime getBookingStartTime() {
        return this.bookingStartTime;
    }

    public Booking bookingStartTime(ZonedDateTime bookingStartTime) {
        this.setBookingStartTime(bookingStartTime);
        return this;
    }

    public void setBookingStartTime(ZonedDateTime bookingStartTime) {
        this.bookingStartTime = bookingStartTime;
    }

    public ZonedDateTime getBookingEndTime() {
        return this.bookingEndTime;
    }

    public Booking bookingEndTime(ZonedDateTime bookingEndTime) {
        this.setBookingEndTime(bookingEndTime);
        return this;
    }

    public void setBookingEndTime(ZonedDateTime bookingEndTime) {
        this.bookingEndTime = bookingEndTime;
    }

    

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Booking)) {
            return false;
        }
        return getId() != null && getId().equals(((Booking) o).getId());
    }

    @Override
    public int hashCode() {
        // see
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Booking{" +
                "id=" + getId() +
                ", bundleId=" + getBundleId() +
                ", customerName='" + getCustomerName() + "'" +
                ", bookingStartDate='" + getBookingStartDate() + "'" +
                ", bookingEndDate='" + getBookingEndDate() + "'" +
                ", bookingStartTime='" + getBookingStartTime() + "'" +
                ", bookingEndTime='" + getBookingEndTime() + "'" +
                "}";
    }
}
