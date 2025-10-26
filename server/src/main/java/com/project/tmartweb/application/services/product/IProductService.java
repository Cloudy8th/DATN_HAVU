package com.project.tmartweb.application.services.product;

import com.project.tmartweb.application.services.base.IBaseService;
import com.project.tmartweb.application.services.base.IBaseServiceMultiple;
import com.project.tmartweb.domain.dtos.ProductDTO;
import com.project.tmartweb.domain.entities.Product;
import com.project.tmartweb.domain.paginate.PaginationDTO;

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
