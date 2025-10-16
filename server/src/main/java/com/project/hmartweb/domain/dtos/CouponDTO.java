package com.project.hmartweb.domain.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CouponDTO {
    @NotBlank(message = "Mã giảm giá không được để trống!")
    private String code;

    private double discount;

    private Timestamp expirationDate;

    private Boolean expired;

    @Min(0)
    private int quantity;
}
