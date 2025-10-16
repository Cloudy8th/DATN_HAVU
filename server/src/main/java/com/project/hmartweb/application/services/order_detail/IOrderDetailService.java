package com.project.hmartweb.application.services.order_detail;

import com.project.hmartweb.application.responses.OrderDetailResponse;
import com.project.hmartweb.application.services.base.IBaseService;
import com.project.hmartweb.domain.dtos.OrderDetailDTO;
import com.project.hmartweb.domain.entities.OrderDetail;

import java.util.List;
import java.util.UUID;

public interface IOrderDetailService extends IBaseService<OrderDetail, OrderDetailDTO, UUID> {
    List<OrderDetailResponse> getAllByOrder(UUID id);
}
