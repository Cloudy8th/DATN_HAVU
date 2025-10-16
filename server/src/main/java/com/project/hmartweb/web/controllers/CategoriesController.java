package com.project.hmartweb.web.controllers;

import com.project.hmartweb.application.services.category.ICategoryService;
import com.project.hmartweb.domain.dtos.CategoryDTO;
import com.project.hmartweb.domain.entities.Category;
import com.project.hmartweb.web.base.RestAPI;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestAPI("${api.prefix}/categories")
public class CategoriesController {
    @Autowired
    private ICategoryService iCategoryService;

    @GetMapping("")
    public ResponseEntity<?> getAllCategories() {
        var categories = iCategoryService.getAll(null, null);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable UUID id) {
        Category category = iCategoryService.getById(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> insertCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        Category category = iCategoryService.insert(categoryDTO);
        return ResponseEntity.status(201).body(category);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryDTO categoryDTO) {
        var result = iCategoryService.update(id, categoryDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCategory(@PathVariable UUID id) {
        Category category = iCategoryService.getById(id);
        iCategoryService.delete(category);
        return ResponseEntity.ok("Xóa thành công");
    }
}
