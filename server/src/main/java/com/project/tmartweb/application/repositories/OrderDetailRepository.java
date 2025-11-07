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

    @Query("SELECT NEW com.project.tmartweb.application.responses.ProductSalesStatistical(od.product.id, od.product.title, SUM(od.quantity), SUM(od.totalMoney)) " +
            "FROM OrderDetail od JOIN od.order o " + // JOIN vá»›i Order Ä‘á»ƒ lá»c status
            "WHERE o.status = 'SHIPPED' AND o.createdAt BETWEEN :startDate AND :endDate " + // Chá»‰ tÃ­nh cÃ¡c Ä‘Æ¡n Ä‘Ã£ giao
            "GROUP BY od.product.id, od.product.title " + // NhÃ³m theo sáº£n pháº©m
            "ORDER BY SUM(od.quantity) DESC") // Sáº¯p xáº¿p theo sá»‘ lÆ°á»£ng bÃ¡n cháº¡y nháº¥t
    List<ProductSalesStatistical> statisticalByProduct(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);
}