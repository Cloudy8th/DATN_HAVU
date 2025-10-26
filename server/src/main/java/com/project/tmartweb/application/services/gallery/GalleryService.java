package com.project.tmartweb.application.services.gallery;

import com.project.tmartweb.application.repositories.GalleryRepository;
import com.project.tmartweb.application.services.file.IFileService;
import com.project.tmartweb.application.services.product.IProductService;
import com.project.tmartweb.config.exceptions.NotFoundException;
import com.project.tmartweb.domain.dtos.GalleryDTO;
import com.project.tmartweb.domain.entities.Gallery;
import com.project.tmartweb.domain.entities.Product;
import com.project.tmartweb.domain.paginate.BasePagination;
import com.project.tmartweb.domain.paginate.PaginationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GalleryService implements IGalleryService {
    private final GalleryRepository galleryRepository;
    private final IProductService productService;
    private final IFileService fileService;

    @Override
    public Gallery insert(GalleryDTO galleryDTO) {
        return null;
    }

    @Override
    public Gallery update(UUID id, GalleryDTO galleryDTO) {
        Gallery gallery = getById(id);
        Product product = productService.getById(galleryDTO.getProductId());
        gallery.setProduct(product);
        return galleryRepository.save(gallery);
    }

    @Override
    public void delete(Gallery gallery) {
        galleryRepository.delete(gallery);
    }

    @Override
    public PaginationDTO<Gallery> getAll(Integer page, Integer perPage) {
        if (page == null && perPage == null) {
            return new PaginationDTO<>(
                    galleryRepository.findAll(Sort.by("createdAt").descending()), null);
        }
        BasePagination<Gallery, GalleryRepository> pagination = new BasePagination<>(galleryRepository);
        return pagination.paginate(page, perPage);
    }

    @Override
    public Optional<Gallery> findById(UUID id) {
        return galleryRepository.findById(id);
    }

    @Override
    public Gallery getById(UUID id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Trưng bày này không tồn tại",
                                                                    "Gallery not found"));
    }

    @Override
    public Gallery insert(String productId, MultipartFile image) {
        Product product = productService.getById(productId);
        Gallery gallery = new Gallery();
        gallery.setProduct(product);
        gallery.setImage(fileService.uploadFile(image));
        return galleryRepository.save(gallery);
    }

    @Override
    public Gallery update(UUID id, String productId, MultipartFile image) {
        Product product = productService.getById(productId);
        Gallery gallery = getById(id);
        gallery.setProduct(product);
        gallery.setImage(fileService.uploadFile(image));
        return galleryRepository.save(gallery);
    }
}
