package com.project.hmartweb.application.services.product;

import com.project.hmartweb.application.services.base.IBaseService;
import com.project.hmartweb.application.services.base.IBaseServiceMultiple;
import com.project.hmartweb.domain.dtos.ProductDTO;
import com.project.hmartweb.domain.entities.Product;
import com.project.hmartweb.domain.paginate.PaginationDTO;

import java.util.UUID;

public interface IProductService
        extends IBaseService<Product, ProductDTO, String>,
        IBaseServiceMultiple<Product, ProductDTO, String> {
    PaginationDTO<Product> getAllProductsByCategory(UUID categoryId, Integer page, Integer perPage);

    PaginationDTO<Product> getAllDeleted(Integer page, Integer perPage);

    PaginationDTO<Product> getAllBySoldQuantity(Integer page, Integer perPage);

    PaginationDTO<Product> getAllByDiscount(Integer page, Integer perPage);

    PaginationDTO<Product> getAllBySearch(
            String keyword, String direction, String price,
            Integer page, Integer perPage);

    PaginationDTO<Product> getAllByFilter(
            String keyword, String title, String discount, String price,
            String productId, UUID categoryId, boolean isStock,
            Integer page, Integer perPage
    );
}
