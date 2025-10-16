package com.project.hmartweb.application.repositories;

import com.project.hmartweb.domain.entities.OrderDetail;
import com.project.hmartweb.domain.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, UUID> {
    Page<OrderDetail> findAllByOrderId(UUID orderId, Pageable pageable);

    List<OrderDetail> findAllByOrderId(UUID orderId);

    List<OrderDetail> findAllByProduct(Product product);
}
