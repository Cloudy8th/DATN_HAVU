package com.project.hmartweb.application.services.order_detail;

import com.project.hmartweb.application.repositories.OrderDetailRepository;
import com.project.hmartweb.application.repositories.OrderRepository;
import com.project.hmartweb.application.responses.OrderDetailResponse;
import com.project.hmartweb.application.services.product.IProductService;
import com.project.hmartweb.config.exceptions.NotFoundException;
import com.project.hmartweb.config.helpers.Calculator;
import com.project.hmartweb.domain.dtos.OrderDetailDTO;
import com.project.hmartweb.domain.entities.Order;
import com.project.hmartweb.domain.entities.OrderDetail;
import com.project.hmartweb.domain.entities.Product;
import com.project.hmartweb.domain.paginate.BasePagination;
import com.project.hmartweb.domain.paginate.PaginationDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderDetailService implements IOrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final IProductService productService;
    private final OrderRepository orderRepository;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public OrderDetail insert(OrderDetailDTO orderDetailDTO) {
        OrderDetail orderDetail = mapper.map(orderDetailDTO, OrderDetail.class);
        Product product = productService.getById(orderDetailDTO.getProductId());
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                                     .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn taị!",
                                                                              "Order not found"));
        orderDetail.setProduct(product);
        orderDetail.setOrder(order);
        orderDetail.setTotalMoney(
                Calculator.totalMoney(
                        product.getSalePrice(), orderDetail.getQuantity()
                ));
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail update(UUID id, OrderDetailDTO orderDetailDTO) {
        OrderDetail orderDetail = getById(id);
        mapper.map(orderDetailDTO, orderDetail);
        Product product = productService.getById(orderDetailDTO.getProductId());
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                                     .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn taị!",
                                                                              "Order not found"));
        orderDetail.setProduct(product);
        orderDetail.setOrder(order);
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public void delete(OrderDetail orderDetail) {
    }

    @Override
    public PaginationDTO<OrderDetail> getAll(Integer page, Integer perPage) {
        if (page == null && perPage == null) {
            return new PaginationDTO<>(orderDetailRepository.findAll(), null);
        }
        BasePagination<OrderDetail, OrderDetailRepository> basePagination = new BasePagination<>(orderDetailRepository);
        return basePagination.paginate(page, perPage);
    }

    @Override
    public Optional<OrderDetail> findById(UUID id) {
        return orderDetailRepository.findById(id);
    }

    @Override
    public OrderDetail getById(UUID id) {
        return findById(id).
                orElseThrow(() -> new NotFoundException("Chi tiết đơn hàng không tồn tại!", "Order detail not found"));
    }

    @Override
    public List<OrderDetailResponse> getAllByOrder(UUID id) {
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderId(id);
        List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetails) {
            OrderDetailResponse orderDetailResponse = mapper.map(orderDetail, OrderDetailResponse.class);
            orderDetailResponse.setProduct(orderDetail.getProduct());
            orderDetailResponses.add(orderDetailResponse);
        }
        return orderDetailResponses;
    }
}
