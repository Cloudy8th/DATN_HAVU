package com.project.tmartweb.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.tmartweb.domain.entities.base.AbstractAuditingEntity;
import com.project.tmartweb.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order extends AbstractAuditingEntity {
    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "phone_number", length = 12)
    private String phoneNumber;

    @Column(name = "address", length = 250)
    private String address;

    @Column(name = "note", length = 200)
    private String note;

    @Column(name = "status", length = 100)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "is_feedback")
    private boolean isFeedback = false;

    @Column(name = "total_money")
    private double totalMoney;

    @Column(name = "payment_method")
    private String paymentMethod;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "coupon_code")
    private Coupon coupon;

    @JsonManagedReference
    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;
}
