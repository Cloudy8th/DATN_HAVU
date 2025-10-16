package com.project.hmartweb.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.hmartweb.domain.entities.base.AbstractAuditingEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "feedbacks")
public class Feedback extends AbstractAuditingEntity {

    @Column(name = "note")
    private String note;

    @Column(name = "star")
    @Min(0)
    @Max(5)
    private int star = 0;

    @Column(name = "image")
    private String image;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}
