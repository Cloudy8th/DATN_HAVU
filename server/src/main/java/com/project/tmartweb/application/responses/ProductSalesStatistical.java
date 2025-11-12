// ProductSalesStatistical.java
package com.project.tmartweb.application.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductSalesStatistical {
    private String productId;
    private String productTitle;
    private Long totalQuantitySold;
    private Double totalRevenue;
}