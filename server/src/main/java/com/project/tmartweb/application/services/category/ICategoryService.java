package com.project.tmartweb.application.services.category;

import com.project.tmartweb.application.services.base.IBaseService;
import com.project.tmartweb.domain.dtos.CategoryDTO;
import com.project.tmartweb.domain.entities.Category;

import java.util.UUID;

public interface ICategoryService extends IBaseService<Category, CategoryDTO, UUID> {
}
