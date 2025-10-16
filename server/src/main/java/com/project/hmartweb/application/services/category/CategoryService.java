package com.project.hmartweb.application.services.category;

import com.project.hmartweb.application.repositories.CategoryRepository;
import com.project.hmartweb.application.repositories.ProductRepository;
import com.project.hmartweb.config.exceptions.ConflictException;
import com.project.hmartweb.config.exceptions.InvalidParamException;
import com.project.hmartweb.config.exceptions.NotFoundException;
import com.project.hmartweb.domain.dtos.CategoryDTO;
import com.project.hmartweb.domain.entities.Category;
import com.project.hmartweb.domain.entities.Product;
import com.project.hmartweb.domain.paginate.BasePagination;
import com.project.hmartweb.domain.paginate.PaginationDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ModelMapper mapper;

    @Override
    public Category insert(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new ConflictException("Category name already exists");
        }
        Category category = mapper.map(categoryDTO, Category.class);
        return categoryRepository.save(category);
    }

    @Override
    public Category update(UUID id, CategoryDTO categoryDTO) {
        Category category = getById(id);
        mapper.map(categoryDTO, category);
        return categoryRepository.save(category);
    }

    @Override
    public void delete(Category category) {
        List<Product> products = productRepository.findAllByCategory_Id(category.getId());
        if (!products.isEmpty()) {
            throw new InvalidParamException("Danh mục này đang được sử dụng", "Category has products");
        }
        categoryRepository.delete(category);
    }

    @Override
    public PaginationDTO<Category> getAll(Integer page, Integer perPage) {
        if (page == null && perPage == null) {
            return new PaginationDTO<>(categoryRepository.findAll(
                    Sort.by("createdAt").descending()), null);
        }
        BasePagination<Category, CategoryRepository> basePagination = new BasePagination<>(categoryRepository);
        PaginationDTO<Category> pageRequest = basePagination.paginate(page, perPage);
        return new PaginationDTO<>(pageRequest.getData(), pageRequest.getPagination());
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category getById(UUID id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
    }
}
