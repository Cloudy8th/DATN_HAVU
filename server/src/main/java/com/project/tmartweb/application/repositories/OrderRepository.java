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

    @Query("SELECT NEW com.project.tmartweb.application.responses.Statistical(EXTRACT(MONTH FROM o.createdAt), SUM(o.totalMoney)) " +
            "FROM Order o " +
            "WHERE EXTRACT(YEAR FROM o.createdAt) = :year AND o.status = 'SHIPPED'" +
            "GROUP BY EXTRACT(MONTH FROM o.createdAt)")
    List<Statistical> statistical(@Param("year") int year);

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
