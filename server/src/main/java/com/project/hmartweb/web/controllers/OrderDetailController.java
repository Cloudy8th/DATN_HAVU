package com.project.hmartweb.web.controllers;

import com.project.hmartweb.application.responses.OrderDetailResponse;
import com.project.hmartweb.application.services.order_detail.IOrderDetailService;
import com.project.hmartweb.web.base.RolesAdminUser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/order-details")
public class OrderDetailController {
    @Autowired
    private IOrderDetailService orderDetailService;
    @Autowired
    private ModelMapper mapper;

    @GetMapping("")
    @RolesAdminUser
    public ResponseEntity<?> getAllOrderDetails(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer perPage
    ) {
        var result = orderDetailService.getAll(page, perPage);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @RolesAdminUser
    public ResponseEntity<?> getOrderDetail(@PathVariable UUID id) {
        var result = orderDetailService.getById(id);
        OrderDetailResponse orderDetailResponse = mapper.map(result, OrderDetailResponse.class);
        return ResponseEntity.ok(orderDetailResponse);
    }

    @GetMapping("/order/{id}")
    @RolesAdminUser
    public ResponseEntity<?> getAllOrderDetailsByOrder(
            @PathVariable UUID id
    ) {
        var result = orderDetailService.getAllByOrder(id);
        return ResponseEntity.ok(result);
    }
}
