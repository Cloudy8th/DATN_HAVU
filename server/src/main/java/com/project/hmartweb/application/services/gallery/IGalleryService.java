package com.project.hmartweb.application.services.gallery;

import com.project.hmartweb.application.services.base.IBaseService;
import com.project.hmartweb.domain.dtos.GalleryDTO;
import com.project.hmartweb.domain.entities.Gallery;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface IGalleryService extends IBaseService<Gallery, GalleryDTO, UUID> {

    Gallery insert(String productId, MultipartFile image);

    Gallery update(UUID id, String productId, MultipartFile image);
}
