package com.project.hmartweb.application.services.category;

import com.project.hmartweb.application.services.base.IBaseService;
import com.project.hmartweb.domain.dtos.CategoryDTO;
import com.project.hmartweb.domain.entities.Category;

import java.util.UUID;

public interface ICategoryService extends IBaseService<Category, CategoryDTO, UUID> {
}
