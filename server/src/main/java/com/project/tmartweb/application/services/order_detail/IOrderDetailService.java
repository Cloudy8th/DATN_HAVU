package com.project.tmartweb.application.services.order_detail;

import com.project.tmartweb.application.responses.OrderDetailResponse;
import com.project.tmartweb.application.services.base.IBaseService;
import com.project.tmartweb.domain.dtos.OrderDetailDTO;
import com.project.tmartweb.domain.entities.OrderDetail;

import java.util.List;
import java.util.UUID;

public interface IOrderDetailService extends IBaseService<OrderDetail, OrderDetailDTO, UUID> {
    List<OrderDetailResponse> getAllByOrder(UUID id);
}
