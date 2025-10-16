package com.project.hmartweb.application.repositories;

import com.project.hmartweb.domain.entities.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, String> {
}
