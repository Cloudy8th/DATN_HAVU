package com.project.hmartweb.domain.entities;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ProductIdGenerator {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public String generateNextId() {
        List<String> resultList = entityManager
                .createQuery("SELECT p.id FROM Product p " +
                        "ORDER BY CAST(SUBSTRING(p.id, 9) AS int) DESC", String.class)
                .setMaxResults(1)
                .getResultList();

        String maxId = resultList.isEmpty() ? null : resultList.get(0);
        int nextNumber = 1;

        if (maxId != null && maxId.startsWith("PRODUCT-")) {
            String numberPart = maxId.substring(8);
            nextNumber = Integer.parseInt(numberPart) + 1;
        }

        return String.format("PRODUCT-%d", nextNumber);
    }
}