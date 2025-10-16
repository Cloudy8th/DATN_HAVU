package com.project.hmartweb.domain.entities;

import com.project.hmartweb.domain.entities.base.AbstractAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category extends AbstractAuditingEntity {
    @Column(name = "name", length = 100)
    private String name;
}
