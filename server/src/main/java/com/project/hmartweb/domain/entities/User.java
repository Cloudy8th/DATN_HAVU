package com.project.hmartweb.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.hmartweb.domain.entities.base.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends AbstractAuditingEntity {
    @Column(name = "user_name", unique = true)
    private String userName;

    @JsonIgnore
    @Column(name = "password", length = 200)
    private String password;

    @Column(name = "image", length = 500)
    private String image;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "phone_number", length = 12)
    private String phoneNumber;

    @Column(name = "email", length = 120)
    private String email;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "deleted")
    private Boolean deleted = Boolean.FALSE;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @JsonIgnore
    @OneToOne
    @Transient
    private Token token;
}
