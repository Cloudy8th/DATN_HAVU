package com.project.hmartweb.application.services.base;

import com.project.hmartweb.domain.paginate.PaginationDTO;

import java.util.Optional;

public interface IBaseServiceReadOnly<Entity, DataType> {
    PaginationDTO<Entity> getAll(Integer page, Integer perPage);

    Optional<Entity> findById(DataType id);

    Entity getById(DataType id);
}
