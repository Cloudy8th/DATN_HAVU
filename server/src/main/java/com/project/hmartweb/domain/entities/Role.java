package com.project.hmartweb.domain.entities;

import com.project.hmartweb.domain.enums.RoleId;
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
@Table(name = "roles")
public class Role {
    @Id
    @Column(name = "id", length = 50)
    @Enumerated(EnumType.STRING)
    private RoleId id;

    @Column(name = "role_name", length = 20)
    private String roleName;
}
