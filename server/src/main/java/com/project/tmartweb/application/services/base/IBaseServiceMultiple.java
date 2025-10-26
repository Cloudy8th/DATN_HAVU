package com.project.tmartweb.application.services.base;

import java.util.List;

public interface IBaseServiceMultiple<Entity, EntityDTO, Type> {
    List<Entity> insertMultiple(List<EntityDTO> dtos);

    List<Entity> updateMultiple(List<EntityDTO> dtos);

    int deleteMultiple(List<Type> types);
}
