package com.project.hmartweb.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @Column(name = "token", length = 255)
    private String token;

    @Column(name = "token_type", length = 50)
    private String tokenType;

    @Column(name = "expiration_date")
    private Timestamp expirationDate;

    @Column(name = "expired")
    private Boolean expired = Boolean.FALSE;

    @Column(name = "revoked")
    private Boolean revoked = Boolean.FALSE;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;
}
