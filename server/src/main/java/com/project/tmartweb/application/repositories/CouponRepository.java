package com.project.tmartweb.application.repositories;

import com.project.tmartweb.domain.entities.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, String> {
}
