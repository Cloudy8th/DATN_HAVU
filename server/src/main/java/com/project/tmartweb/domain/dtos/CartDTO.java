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
public class CartDTO {
    private UUID id;

    @Min(1)
    private int quantity;

    private UUID userId;

    private String productId;

    private String classify;
}
