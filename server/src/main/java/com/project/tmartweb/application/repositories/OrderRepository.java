package com.project.tmartweb.application.repositories;

import com.project.tmartweb.application.responses.Statistical;
import com.project.tmartweb.domain.entities.Order;
import com.project.tmartweb.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.project.tmartweb.application.responses.RevenueByDate;
import com.project.tmartweb.application.responses.RevenueByWeek;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query("SELECT ord from Order ord inner join OrderDetail od " +
            "on ord.id = od.order.id where ord.user.id = :userId " +
            "and ((ord.status = :status or :status is null) and " +
            "(lower(od.product.title) like lower(concat('%', :keyword, '%')) " +
            "or :keyword is null)) order by ord.createdAt desc")
    List<Order> findByUserId(@Param("userId") UUID userId,
                             @Param("status") OrderStatus status,
                             @Param("keyword") String keyword);

    @Query("SELECT NEW com.project.tmartweb.application.responses.Statistical(EXTRACT(YEAR FROM o.createdAt), EXTRACT(MONTH FROM o.createdAt), SUM(o.totalMoney)) " +
            "FROM Order o " +
            "WHERE o.status = 'SHIPPED' AND o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY EXTRACT(YEAR FROM o.createdAt), EXTRACT(MONTH FROM o.createdAt) " +
            "ORDER BY EXTRACT(YEAR FROM o.createdAt), EXTRACT(MONTH FROM o.createdAt)")
    List<Statistical> statisticalByMonth(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);

    // Má»šI: Thá»‘ng kÃª theo ngÃ y
    @Query("SELECT NEW com.project.tmartweb.application.responses.RevenueByDate(CAST(o.createdAt AS DATE), SUM(o.totalMoney)) " +
            "FROM Order o " +
            "WHERE o.status = 'SHIPPED' AND o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(o.createdAt AS DATE) " +
            "ORDER BY CAST(o.createdAt AS DATE)")
    List<RevenueByDate> statisticalByDay(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);

    // Má»šI: Thá»‘ng kÃª theo tuáº§n
    @Query("SELECT NEW com.project.tmartweb.application.responses.RevenueByWeek(EXTRACT(YEAR FROM o.createdAt), EXTRACT(WEEK FROM o.createdAt), SUM(o.totalMoney)) " +
            "FROM Order o " +
            "WHERE o.status = 'SHIPPED' AND o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY EXTRACT(YEAR FROM o.createdAt), EXTRACT(WEEK FROM o.createdAt) " +
            "ORDER BY EXTRACT(YEAR FROM o.createdAt), EXTRACT(WEEK FROM o.createdAt)")
    List<RevenueByWeek> statisticalByWeek(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);

    @Query("select o from Order o " +
            "where (cast(:startDate as timestamp) is null or " +
            "cast(:endDate as timestamp) is null or " +
            "(o.createdAt >= :startDate and o.createdAt <= :endDate)) " +
            "and (:status is null or o.status = :status) " +
            "order by o.createdAt desc")
    Page<Order> findAllByFilter(@Param("startDate") Timestamp startDate,
                                @Param("endDate") Timestamp endDate,
                                @Param("status") OrderStatus status,
                                Pageable pageable);
}