package com.project.hmartweb.application.services.image;

import com.project.hmartweb.application.repositories.ImageProductRepository;
import com.project.hmartweb.application.services.file.FileService;
import com.project.hmartweb.application.services.product.IProductService;
import com.project.hmartweb.config.exceptions.InvalidParamException;
import com.project.hmartweb.domain.entities.ImageProduct;
import com.project.hmartweb.domain.entities.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageProductService implements IImageProductService {
    private final ImageProductRepository imageProductRepository;
    private final FileService fileService;
    private final IProductService productService;

    @Override
    public List<ImageProduct> getByProductId(String productId) {
        return imageProductRepository.findByProductId(productId);
    }

    @Override
    public String uploadImages(String productId, List<MultipartFile> images) {
        Product product = productService.getById(productId);
        for (MultipartFile file : images) {
            String url = fileService.uploadFile(file);
            ImageProduct imageProduct = new ImageProduct();
            imageProduct.setUrl(url);
            imageProduct.setProduct(product);
            imageProductRepository.save(imageProduct);
        }
        return "Upload images success";
    }

    @Override
    public void delete(UUID id) {
        ImageProduct imageProduct = imageProductRepository
                .findById(id).
                orElseThrow(() -> new InvalidParamException(
                        "Hình ảnh sản phẩm đã được cập nhật bởi 1 người dùng khác",
                        "Not found image"));

        imageProductRepository.delete(imageProduct);
    }

    @Override
    public void deleteMultiple(List<UUID> ids) {
        for (UUID id : ids) {
            this.delete(id);
        }
    }
}
