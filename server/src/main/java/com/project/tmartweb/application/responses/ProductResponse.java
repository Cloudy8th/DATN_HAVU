package com.project.tmartweb.application.responses;

import com.project.tmartweb.domain.entities.Feedback;
import com.project.tmartweb.domain.entities.ImageProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private String title;

    private double price;

    private double discount;

    private String description;

    private Boolean deleted;

    private UUID categoryId;

    private List<ImageProduct> imageProducts;

    private List<Feedback> feedbacks;

    private int soldQuantity;
}