package com.project.tmartweb.domain.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {
    @Id
    @Column(name = "id")
    private String id;

    private String title;

    @Column(name = "origin_price")
    private double originPrice;

    @Column(name = "sale_price")
    private double salePrice;

    private double discount;

    private String description;

    @Column(name = "quantity")
    private int quantity;

    private Boolean deleted = Boolean.FALSE;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @JsonManagedReference
    @OneToMany(mappedBy = "product")
    private List<ImageProduct> imageProducts;

    @Transient
    private int soldQuantity;

    @JsonManagedReference
    @OneToMany(mappedBy = "product")
    private List<Feedback> feedbacks;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    public void setSold(int i) {
    }

//    @PrePersist
//    private void prePersist() {
//        if (id == null || id.isEmpty()) {
//            ProductIdGenerator productIdGenerator = new ProductIdGenerator();
//            this.id = productIdGenerator.generateNextId();
//        }
//    }
}
