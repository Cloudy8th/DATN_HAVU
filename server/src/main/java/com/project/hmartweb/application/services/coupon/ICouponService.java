package com.project.hmartweb.application.services.coupon;

import com.project.hmartweb.application.services.base.IBaseService;
import com.project.hmartweb.domain.dtos.CouponDTO;
import com.project.hmartweb.domain.entities.Coupon;

public interface ICouponService extends IBaseService<Coupon, CouponDTO, String> {
    Coupon useCoupon(String code);
}
