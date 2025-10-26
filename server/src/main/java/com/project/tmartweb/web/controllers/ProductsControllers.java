package com.project.tmartweb.web.controllers;

import com.project.tmartweb.application.services.image.IImageProductService;
import com.project.tmartweb.application.services.product.IProductService;
import com.project.tmartweb.domain.dtos.ProductDTO;
import com.project.tmartweb.domain.entities.Product;
import com.project.tmartweb.web.base.RestAPI;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestAPI("${api.prefix}/products")
public class ProductsControllers {
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IImageProductService iImageProductService;

    @GetMapping("")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer perPage
    ) {
        return ResponseEntity.ok(iProductService.getAll(page, perPage));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "perPage") int perPage,
            @RequestParam(name = "feedback", defaultValue = "asc") String feedback,
            @RequestParam(name = "price", defaultValue = "asc") String price
    ) {
        var res = iProductService.getAllBySearch(keyword, feedback, price, page, perPage);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterProducts(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "perPage") int perPage,
            @RequestParam(name = "discount", required = false) String discount,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "price", required = false) String price,
            @RequestParam(name = "categoryId", required = false) UUID categoryId,
            @RequestParam(name = "isStock", required = false) boolean isStock,
            @RequestParam(name = "productId", required = false) String productId
    ) {
        var res = iProductService.getAllByFilter(
                keyword, title, discount, price, productId,
                categoryId, isStock, page, perPage);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<?> getProductsByCategoryId(
            @PathVariable UUID id,
            @RequestParam(name = "page", required = false) int page,
            @RequestParam(name = "perPage", required = false) int perPage) {
        var result = iProductService.getAllProductsByCategory(id, page, perPage);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(iProductService.getById(id));
    }

    @GetMapping("/deleted")
    public ResponseEntity<?> getAllDeletedProducts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer perPage
    ) {
        var response = iProductService.getAllDeleted(page, perPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/best-seller")
    public ResponseEntity<?> getAllProductsBySoldQuantity(
            @RequestParam(name = "page", required = false) int page,
            @RequestParam(name = "perPage", required = false) int perPage
    ) {
        var response = iProductService.getAllBySoldQuantity(page, perPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sale")
    public ResponseEntity<?> getAllProductsByDiscount(
            @RequestParam(name = "page", required = false) int page,
            @RequestParam(name = "perPage", required = false) int perPage
    ) {
        var response = iProductService.getAllByDiscount(page, perPage);
        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> insertProduct(
            @Valid @RequestBody ProductDTO productDTO) {
        Product product = iProductService.insert(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PostMapping("/upload-image/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadImage(
            @PathVariable String id,
            @RequestPart("images") List<MultipartFile> files
    ) {
        String result = iImageProductService.uploadImages(id, files);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable String id, @RequestBody ProductDTO productDTO) {
        var result = iProductService.update(id, productDTO);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        Product product = iProductService.getById(id);
        iProductService.delete(product);
        return ResponseEntity.status(HttpStatus.OK).body("Delete success");
    }

    @DeleteMapping("/delete-images")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteImages(@RequestBody List<UUID> uuids) {
        iImageProductService.deleteMultiple(uuids);
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }
}
