package com.project.hmartweb.domain.dtos;

import jakarta.validation.constraints.Max;
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
public class FeedbackDTO {
    private String note;

    @Min(0)
    @Max(5)
    private int star = 0;

    private String image;

    private UUID userId;

    private String productId;
}
