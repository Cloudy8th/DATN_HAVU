package com.project.hmartweb.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "coupons")
public class Coupon {
    @Id
    @Column(name = "code", length = 100)
    private String code;

    @Column(name = "discount")
    private double discount;

    @Column(name = "expiration_date")
    private Timestamp expirationDate;

    @Column(name = "expired")
    private Boolean expired = Boolean.FALSE;

    @Column(name = "quantity")
    @Min(0)
    private int quantity;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;
}
