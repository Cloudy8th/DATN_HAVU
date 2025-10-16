package com.project.hmartweb.web.controllers;

import com.project.hmartweb.application.services.coupon.ICouponService;
import com.project.hmartweb.domain.dtos.CouponDTO;
import com.project.hmartweb.domain.entities.Coupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/coupons")
public class CouponController {
    @Autowired
    private ICouponService couponService;

    @GetMapping("")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getAllCoupons(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer perPage
    ) {
        var result = couponService.getAll(page, perPage);
        return ResponseEntity.status(200).body(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getCoupon(@PathVariable String id) {
        var result = couponService.getById(id);
        return ResponseEntity.status(200).body(result);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> insertCoupon(@RequestBody CouponDTO couponDTO) {
        var result = couponService.insert(couponDTO);
        return ResponseEntity.status(201).body(result);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCoupon(@PathVariable String id, @RequestBody CouponDTO couponDTO) {
        var result = couponService.update(id, couponDTO);
        return ResponseEntity.status(200).body(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCoupon(@PathVariable String id) {
        Coupon coupon = couponService.getById(id);
        couponService.delete(coupon);
        return ResponseEntity.status(200).body("Deleted successfully");
    }

    @GetMapping("/use/{code}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> useCoupon(@PathVariable String code) {
        var result = couponService.useCoupon(code);
        return ResponseEntity.status(200).body(result);
    }
}
