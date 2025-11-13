// OrderDetailRepository.java
package com.project.tmartweb.application.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.project.tmartweb.domain.entities.OrderDetail;
import com.project.tmartweb.domain.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.tmartweb.application.responses.ProductSalesStatistical;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, UUID> {

    Page<OrderDetail> findAllByOrderId(UUID orderId, Pageable pageable);

    List<OrderDetail> findAllByOrderId(UUID orderId);

    List<OrderDetail> findAllByProduct(Product product);

    // ✅ Thống kê sản phẩm bán được trong khoảng thời gian (chỉ tính đơn hàng đã giao)
    @Query("""
        SELECT NEW com.project.tmartweb.application.responses.ProductSalesStatistical(
            od.product.id,
            od.product.title,
            CAST(SUM(od.quantity) AS long),
            CAST(SUM(od.totalMoney) AS double)
        )
        FROM OrderDetail od 
        JOIN od.order o
        WHERE o.status = com.project.tmartweb.domain.enums.OrderStatus.SHIPPED 
          AND o.createdAt BETWEEN :startDate AND :endDate
        GROUP BY od.product.id, od.product.title
        ORDER BY SUM(od.quantity) DESC
    """)
    List<ProductSalesStatistical> statisticalByProduct(
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate
    );

    // ✅ FIX: Tính tổng số lượng đã bán của 1 sản phẩm (chỉ tính đơn hàng đã giao)
    @Query("""
        SELECT SUM(od.quantity) 
        FROM OrderDetail od 
        JOIN od.order o 
        WHERE od.product = :product 
          AND o.status = com.project.tmartweb.domain.enums.OrderStatus.SHIPPED
    """)
    Long sumSoldQuantityByProduct(@Param("product") Product product);
}
