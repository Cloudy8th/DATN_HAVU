package com.project.tmartweb.application.services.base;

import com.project.tmartweb.domain.paginate.PaginationDTO;

import java.util.Optional;

public interface IBaseServiceReadOnly<Entity, DataType> {
    PaginationDTO<Entity> getAll(Integer page, Integer perPage);

    Optional<Entity> findById(DataType id);

    Entity getById(DataType id);
}
