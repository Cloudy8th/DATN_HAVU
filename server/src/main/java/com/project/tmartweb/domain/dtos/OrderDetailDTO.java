package com.project.tmartweb.domain.dtos;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDetailDTO {
    @Min(0)
    private double price;

    @Min(1)
    private int quantity;

    private String classify;

    private UUID orderId;

    private String productId;
}
