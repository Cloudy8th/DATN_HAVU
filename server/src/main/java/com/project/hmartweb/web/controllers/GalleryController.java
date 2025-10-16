package com.project.hmartweb.web.controllers;

import com.project.hmartweb.application.services.gallery.IGalleryService;
import com.project.hmartweb.web.base.RestAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestAPI("${api.prefix}/galleries")
public class GalleryController {
    @Autowired
    private IGalleryService galleryService;

    @GetMapping("")
    public ResponseEntity<?> getAllGalleries() {
        var res = galleryService.getAll(null, null);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGalleryById(@PathVariable UUID id) {
        var res = galleryService.getById(id);
        return ResponseEntity.ok(res);
    }

    @PostMapping("")
    public ResponseEntity<?> insertGallery(
            @RequestParam("productId") String productId,
            @RequestPart("image") MultipartFile image
    ) {
        var res = galleryService.insert(productId, image);
        return ResponseEntity.status(201).body(res);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGallery(
            @PathVariable UUID id,
            @RequestParam("productId") String productId,
            @RequestPart("image") MultipartFile image
    ) {
        var res = galleryService.update(id, productId, image);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGallery(@PathVariable UUID id) {
        galleryService.delete(galleryService.getById(id));
        return ResponseEntity.ok("Deleted");
    }
}
