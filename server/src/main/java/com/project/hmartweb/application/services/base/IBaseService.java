package com.project.hmartweb.application.services.base;

public interface IBaseService<Entity, EntityDTO, DataType> extends IBaseServiceReadOnly<Entity, DataType> {
    Entity insert(EntityDTO entityDTO);

    Entity update(DataType id, EntityDTO entityDTO);

    void delete(Entity entity);
}
