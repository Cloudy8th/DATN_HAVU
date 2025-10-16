package com.project.hmartweb.application.services.cart;

import com.project.hmartweb.application.services.base.IBaseService;
import com.project.hmartweb.application.services.base.IBaseServiceMultiple;
import com.project.hmartweb.domain.dtos.CartDTO;
import com.project.hmartweb.domain.entities.Cart;
import com.project.hmartweb.domain.paginate.PaginationDTO;

import java.util.UUID;

public interface ICartService extends IBaseService<Cart, CartDTO, UUID>,
        IBaseServiceMultiple<Cart, CartDTO, UUID> {
    PaginationDTO<Cart> getAllByUser(UUID userId, Integer page, Integer perPage);
}
