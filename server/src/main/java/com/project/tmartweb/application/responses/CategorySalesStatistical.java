package com.project.tmartweb.application.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

/**
 * Thống kê doanh thu theo DANH MỤC sản phẩm
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategorySalesStatistical {
    private UUID categoryId;
    private String categoryName;
    private Long totalQuantitySold;    // Tổng số lượng bán
    private Double totalRevenue;       // Tổng doanh thu
}