package com.project.hmartweb.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.hmartweb.domain.entities.base.AbstractAuditingEntity;
import jakarta.persistence.*;
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
@Table(name = "order_detail")
public class OrderDetail extends AbstractAuditingEntity {
    @Column(name = "price")
    @Min(0)
    private double price;

    @Column(name = "quantity")
    @Min(1)
    private int quantity;

    @Column(name = "total_money")
    @Min(0)
    private double totalMoney;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "classify")
    private String classify;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
