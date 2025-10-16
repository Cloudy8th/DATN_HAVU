package com.project.hmartweb.domain.entities;

import com.project.hmartweb.domain.entities.base.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "notifications")
public class Notification extends AbstractAuditingEntity {
    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "read")
    private Boolean read = Boolean.FALSE;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
