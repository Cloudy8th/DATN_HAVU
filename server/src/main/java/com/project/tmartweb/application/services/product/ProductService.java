package com.project.tmartweb.application.services.product;

import com.project.tmartweb.application.repositories.OrderDetailRepository;
import com.project.tmartweb.application.repositories.ProductRepository;
import com.project.tmartweb.application.services.category.CategoryService;
import com.project.tmartweb.config.exceptions.InvalidParamException;
import com.project.tmartweb.config.exceptions.NotFoundException;
import com.project.tmartweb.domain.dtos.ProductDTO;
import com.project.tmartweb.domain.entities.Category;
import com.project.tmartweb.domain.entities.Product;
import com.project.tmartweb.domain.entities.ProductIdGenerator;
import com.project.tmartweb.domain.paginate.BasePagination;
import com.project.tmartweb.domain.paginate.Pagination;
import com.project.tmartweb.domain.paginate.PaginationDTO;
import com.project.tmartweb.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper mapper;
    private final ProductIdGenerator productIdGenerator;
    private final PaginationUtils paginationUtils;

    @Override
    public PaginationDTO<Product> getAllProductsByCategory(UUID categoryId, Integer page, Integer perPage) {
        PageRequest pageRequest = PageRequest.of(page, perPage, Sort.by("createdAt").descending());
        Page<Product> pageData = productRepository.findAllByCategory_IdAndDeleted(categoryId, false, pageRequest);
        setSoldQuantity(pageData.getContent());
        BasePagination<Product, ProductRepository> pagination = new BasePagination<>(productRepository);
        return pagination.paginate(page, perPage, pageData);
    }

    @Override
    public PaginationDTO<Product> getAllDeleted(Integer page, Integer perPage) {
        if (page == null || perPage == null) {
            List<Product> products = productRepository.findAllByDeleted(
                    true,
                    Sort.by("createdAt").descending());
            setSoldQuantity(products);
            return new PaginationDTO<>(products, null);
        }
        Page<Product> pageData = productRepository.findAllByDeleted(
                true,
                PageRequest.of(page, perPage, Sort.by("createdAt").descending()));
        setSoldQuantity(pageData.getContent());
        BasePagination<Product, ProductRepository> pagination = new BasePagination<>(productRepository);
        return pagination.paginate(page, perPage, pageData);
    }

    @Override
    public PaginationDTO<Product> getAllBySoldQuantity(Integer page, Integer perPage) {
        Page<Product> products = productRepository.findAllBySoldQuantity(PageRequest.of(page, perPage));
        setSoldQuantity(products.getContent());
        BasePagination<Product, ProductRepository> pagination = new BasePagination<>();
        return pagination.paginate(page, perPage, products);
    }

    @Override
    public PaginationDTO<Product> getAllByDiscount(Integer page, Integer perPage) {
        Page<Product> products = productRepository.findAllByDiscount(PageRequest.of(page, perPage));
        setSoldQuantity(products.getContent());
        BasePagination<Product, ProductRepository> pagination = new BasePagination<>();
        return pagination.paginate(page, perPage, products);
    }

    @Override
    public PaginationDTO<Product> getAllBySearch(String keyword, String feedback, String price, Integer page, Integer perPage) {
        Page<Product> products = productRepository.findAllBySearch(
                keyword, feedback, price,
                PageRequest.of(page, perPage));
        BasePagination<Product, ProductRepository> pagination = new BasePagination<>();
        return pagination.paginate(page, perPage, products);
    }

    @Override
    public PaginationDTO<Product> getAllByFilter(String keyword, String title, String discount, String price,
                                                 String productId, UUID categoryId, boolean isStock,
                                                 Integer page, Integer perPage) {
        String productIdQuery = null;
        if (productId != null && !productId.isBlank()) {
            productIdQuery = productId;
        }
        List<Sort.Order> orders = new ArrayList<>();
        paginationUtils.addOrderIfPresent(orders, "title", title);
        paginationUtils.addOrderIfPresent(orders, "discount", discount);
        paginationUtils.addOrderIfPresent(orders, "salePrice", price);
        orders.add(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Page<Product> products = productRepository.findAllByFilter(
                keyword, productIdQuery, categoryId, isStock,
                PageRequest.of(page, perPage, Sort.by(orders)));
        setSoldQuantity(products.getContent());
        BasePagination<Product, ProductRepository> pagination = new BasePagination<>();
        return pagination.paginate(page, perPage, products);
    }

    @Override
    public Product insert(ProductDTO productDTO) {
        Category category = categoryService.getById(productDTO.getCategoryId());
        Product product = mapper.map(productDTO, Product.class);
        double salePrice = product.getSalePrice();
        double discount = 0;
        double originPrice = product.getOriginPrice();
        if (salePrice > originPrice) {
            throw new InvalidParamException("Giá bán không thể lớn hơn giá gốc",
                    "Sale price must be less than or equal to origin price");
        }
        if (salePrice > 0 && salePrice != product.getOriginPrice()) {
            discount = 100 - ((salePrice / product.getOriginPrice()) * 100);
        } else if (salePrice == 0 && product.getOriginPrice() > 0) {
            product.setSalePrice(product.getOriginPrice());
        }
        product.setId(productIdGenerator.generateNextId());
        product.setDiscount(Math.round(discount));
        product.setCategory(category);
        return productRepository.save(product);
    }

    @Override
    public Product update(String id, ProductDTO productDTO) {
        Product product = getById(id);
        Category category = categoryService.getById(productDTO.getCategoryId());
        mapper.map(productDTO, product);
        double salePrice = product.getSalePrice();
        double discount = 0;
        double originPrice = product.getOriginPrice();
        if (salePrice > originPrice) {
            throw new InvalidParamException("Giá bán không thể lớn hơn giá gốc",
                    "Sale price must be less than or equal to origin price");
        }
        if (salePrice > 0 && salePrice != originPrice) {
            discount = 100 - ((salePrice / product.getOriginPrice()) * 100);
        } else if (salePrice == 0 && product.getOriginPrice() > 0) {
            product.setSalePrice(product.getOriginPrice());
        }
        product.setDiscount(Math.round(discount));
        product.setCategory(category);
        return productRepository.save(product);
    }

    @Override
    public void delete(Product product) {
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    public PaginationDTO<Product> getAll(Integer page, Integer perPage) {
        if (page == null || perPage == null) {
            List<Product> products = productRepository.findAllByDeleted(false, Sort.by("createdAt").descending());
            setSoldQuantity(products);
            return new PaginationDTO<>(products, null);
        }
        Page<Product> pageData = productRepository.findAllByDeleted(
                false,
                PageRequest.of(page, perPage, Sort.by("createdAt").descending()));
        setSoldQuantity(pageData.getContent());
        Pagination pagination = new Pagination(page, perPage, pageData.getTotalPages() - 1,
                pageData.getTotalElements());
        return new PaginationDTO<>(pageData.getContent(), pagination);
    }

    @Override
    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }

    @Override
    public Product getById(String id) {
        Product product = findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
        setSoldQuantity(List.of(product)); // Cập nhật số lượng đã bán (chỉ tính đơn SHIPPED)
        return product;
    }

    @Override
    public List<Product> insertMultiple(List<ProductDTO> productDTOS) {
        return List.of();
    }

    @Override
    public List<Product> updateMultiple(List<ProductDTO> productDTOS) {
        return List.of();
    }

    @Override
    public int deleteMultiple(List<String> ids) {
        return 0;
    }

    // ✅ FIXED METHOD: chỉ tính số lượng bán cho đơn hàng đã giao (SHIPPED)
    private void setSoldQuantity(List<Product> products) {
        for (Product product : products) {
            // Sử dụng phương thức mới trong OrderDetailRepository
            Long soldQuantity = orderDetailRepository.sumSoldQuantityByProduct(product);
            product.setSoldQuantity(soldQuantity != null ? soldQuantity.intValue() : 0);
        }
    }
}
