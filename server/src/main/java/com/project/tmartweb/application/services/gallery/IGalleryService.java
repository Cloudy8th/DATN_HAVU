package com.project.tmartweb.application.services.gallery;

import com.project.tmartweb.application.services.base.IBaseService;
import com.project.tmartweb.domain.dtos.GalleryDTO;
import com.project.tmartweb.domain.entities.Gallery;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface IGalleryService extends IBaseService<Gallery, GalleryDTO, UUID> {

    Gallery insert(String productId, MultipartFile image);

    Gallery update(UUID id, String productId, MultipartFile image);
}
