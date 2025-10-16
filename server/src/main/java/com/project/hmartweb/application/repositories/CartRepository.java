package com.project.hmartweb.application.repositories;

import com.project.hmartweb.domain.entities.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    Page<Cart> findAllByUser_Id(UUID userId, PageRequest pageRequest);

    List<Cart> findAllByUser_Id(UUID userId, Sort sort);

    Optional<Cart> findByProductId(String productId);
}
