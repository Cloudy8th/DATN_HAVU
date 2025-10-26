package com.project.tmartweb.application.repositories;

import com.project.tmartweb.domain.entities.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    @Query("select f from Feedback f " +
            "where f.product.id = :id and " +
            "(f.star = :star or :star is null)" +
            "order by f.createdAt desc")
    Page<Feedback> findAllByProductId(String id, Pageable pageable, Integer star);

    @Query("select f from Feedback f " +
            "where f.product.id = :id and " +
            "(f.star = :star or :star is null)" +
            "order by f.createdAt desc")
    List<Feedback> findAllByProductId(String id, Integer star);
}
