package com.project.tmartweb.application.repositories;

import com.project.tmartweb.domain.entities.ImageProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImageProductRepository extends JpaRepository<ImageProduct, UUID> {
    List<ImageProduct> findByProductId(String productId);
}
