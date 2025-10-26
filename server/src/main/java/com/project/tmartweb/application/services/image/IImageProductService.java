package com.project.tmartweb.application.services.image;

import com.project.tmartweb.domain.entities.ImageProduct;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IImageProductService {
    List<ImageProduct> getByProductId(String productId);

    String uploadImages(String productId, List<MultipartFile> images);

    void delete(UUID id);

    void deleteMultiple(List<UUID> ids);
}
