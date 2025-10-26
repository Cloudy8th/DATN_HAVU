package com.project.tmartweb.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private UUID categoryId;

    private String title;

    private double originPrice;

    private double salePrice;

    private double discount;

    private int quantity;

    private String description;

    private Boolean deleted = Boolean.FALSE;
}
