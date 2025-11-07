package com.project.tmartweb.application.responses;

import com.project.tmartweb.domain.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDetailResponse {
    private double price;

    private int quantity;

    private double totalMoney;

    private String classify;

    private Product product;
}