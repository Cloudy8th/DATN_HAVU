package com.project.hmartweb.application.services.coupon;

import com.project.hmartweb.application.repositories.CouponRepository;
import com.project.hmartweb.config.exceptions.InvalidParamException;
import com.project.hmartweb.config.exceptions.NotFoundException;
import com.project.hmartweb.domain.dtos.CouponDTO;
import com.project.hmartweb.domain.entities.Coupon;
import com.project.hmartweb.domain.paginate.BasePagination;
import com.project.hmartweb.domain.paginate.PaginationDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService implements ICouponService {
    private final CouponRepository couponRepository;
    private final ModelMapper mapper;

    @Override
    public Coupon insert(CouponDTO couponDTO) {
        Coupon coupon = mapper.map(couponDTO, Coupon.class);
        return couponRepository.save(coupon);
    }

    @Override
    public Coupon update(String id, CouponDTO couponDTO) {
        Coupon coupon = getById(id);
        mapper.map(couponDTO, coupon);
        return couponRepository.save(coupon);
    }

    @Override
    public void delete(Coupon coupon) {
        couponRepository.delete(coupon);
    }

    @Override
    public PaginationDTO<Coupon> getAll(Integer page, Integer perPage) {
        if (page == null && perPage == null) {
            return new PaginationDTO<>(couponRepository.findAll(), null);
        }
        BasePagination<Coupon, CouponRepository> basePagination = new BasePagination<>(couponRepository);
        return basePagination.paginate(page, perPage);
    }

    @Override
    public Optional<Coupon> findById(String id) {
        return couponRepository.findById(id);
    }

    @Override
    public Coupon getById(String id) {
        return findById(id.toUpperCase()).orElseThrow(() -> new NotFoundException("Mã giảm giá không tồn tại!", "Coupon not found"));
    }

    @Override
    public Coupon useCoupon(String code) {
        Coupon coupon = getById(code);
        if (coupon.getExpired()) {
            throw new InvalidParamException("Mã giảm giá đã hết hạn!", "Coupon is expired");
        }
        if (coupon.getExpirationDate() != null &&
                coupon.getExpirationDate().compareTo(new Timestamp(System.currentTimeMillis())) < 0) {
            coupon.setExpired(true);
            couponRepository.save(coupon);
            throw new InvalidParamException("Mã giảm giá đã hết hạn!", "Coupon is expired");
        }
        if (coupon.getQuantity() == 0) {
            throw new InvalidParamException("Mã giảm giá đã hết lượt sử dụng!", "Coupon is out of stock");
        }
        return coupon;
    }
}
