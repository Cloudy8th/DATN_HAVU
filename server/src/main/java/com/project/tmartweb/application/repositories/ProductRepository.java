// ProductRepository.java
package com.project.tmartweb.application.repositories;

import com.project.tmartweb.domain.entities.Product;
import com.project.tmartweb.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    boolean existsByTitle(String title);

    Page<Product> findAllByCategory_IdAndDeleted(UUID id, boolean deleted, Pageable pageable);

    Page<Product> findAllByDeleted(boolean deleted, Pageable pageable);

    List<Product> findAllByDeleted(boolean deleted, Sort sort);

    List<Product> findAllByCategory_Id(UUID id);

    @Query("""
        SELECT p 
        FROM Product p 
        LEFT JOIN Feedback f ON p.id = f.product.id 
        WHERE p.deleted = false 
          AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR :keyword IS NULL)
        GROUP BY p.id 
        ORDER BY 
          CASE WHEN :feedback = 'asc' THEN COALESCE(AVG(f.star), 0) END ASC,
          CASE WHEN :feedback = 'desc' THEN COALESCE(AVG(f.star), 0) END DESC NULLS LAST,
          CASE WHEN :price = 'asc' THEN p.salePrice END ASC,
          CASE WHEN :price = 'desc' THEN p.salePrice END DESC
    """)
    Page<Product> findAllBySearch(@Param("keyword") String keyword,
                                  @Param("feedback") String feedback,
                                  @Param("price") String price,
                                  Pageable pageable);

    @Query("""
        SELECT p 
        FROM Product p 
        WHERE p.deleted = false 
          AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR :keyword IS NULL)
          AND (:categoryId IS NULL OR p.category.id = :categoryId)
          AND (:isStock = false OR p.quantity > 0)
          AND (:productId IS NULL OR p.id = :productId)
    """)
    Page<Product> findAllByFilter(@Param("keyword") String keyword,
                                  @Param("productId") String productId,
                                  @Param("categoryId") UUID categoryId,
                                  @Param("isStock") boolean isStock,
                                  Pageable pageable);

    @Query("""
        SELECT pr 
        FROM Product pr 
        INNER JOIN OrderDetail od ON pr.id = od.product.id 
        WHERE pr.deleted = false 
        GROUP BY pr.id 
        ORDER BY COUNT(od.id) DESC
    """)
    Page<Product> findAllBySoldQuantity(Pageable pageable);

    @Query("""
        SELECT p 
        FROM Product p 
        WHERE p.discount > 0 
          AND p.deleted = false 
        ORDER BY p.discount DESC
    """)
    Page<Product> findAllByDiscount(Pageable pageable);

    // ✅ FIXED: Truy vấn Best Seller chính xác (lọc theo đơn hàng đã giao)
    @Query("""
        SELECT pr 
        FROM Product pr 
        INNER JOIN OrderDetail od ON pr.id = od.product.id 
        INNER JOIN Order o ON od.order.id = o.id 
        WHERE pr.deleted = false 
          AND o.status = com.project.tmartweb.domain.enums.OrderStatus.SHIPPED 
        GROUP BY pr.id 
        ORDER BY SUM(od.quantity) DESC
    """)
    Page<Product> findAllBestSeller(Pageable pageable);

    // 💡 Bản mở rộng (tùy chọn, dễ bảo trì hơn)
    // @Query("""
    //     SELECT pr
    //     FROM Product pr
    //     INNER JOIN OrderDetail od ON pr.id = od.product.id
    //     INNER JOIN Order o ON od.order.id = o.id
    //     WHERE pr.deleted = false
    //       AND o.status = :status
    //     GROUP BY pr.id
    //     ORDER BY SUM(od.quantity) DESC
    // """)
    // Page<Product> findAllBestSeller(@Param("status") OrderStatus status, Pageable pageable);
}
