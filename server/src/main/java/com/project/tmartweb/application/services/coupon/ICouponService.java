package com.project.tmartweb.application.services.coupon;

import com.project.tmartweb.application.services.base.IBaseService;
import com.project.tmartweb.domain.dtos.CouponDTO;
import com.project.tmartweb.domain.entities.Coupon;

public interface ICouponService extends IBaseService<Coupon, CouponDTO, String> {
    Coupon useCoupon(String code);
}
