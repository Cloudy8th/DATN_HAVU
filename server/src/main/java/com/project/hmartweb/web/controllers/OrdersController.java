package com.project.hmartweb.web.controllers;

import com.project.hmartweb.application.services.order.IOrderExportService;
import com.project.hmartweb.application.services.order.IOrderService;
import com.project.hmartweb.domain.dtos.OrderDTO;
import com.project.hmartweb.domain.entities.Cart;
import com.project.hmartweb.domain.entities.Order;
import com.project.hmartweb.domain.enums.OrderStatus;
import com.project.hmartweb.web.base.RestAPI;
import com.project.hmartweb.web.base.RoleAdmin;
import com.project.hmartweb.web.base.RolesAdminUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@RestAPI("${api.prefix}/orders")
public class OrdersController {
    private final IOrderService orderService;

    private final IOrderExportService orderExportService;

    public OrdersController(IOrderService orderService, IOrderExportService orderExportService) {
        this.orderService = orderService;
        this.orderExportService = orderExportService;
    }

    @GetMapping("")
    @RolesAdminUser
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer perPage
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getAll(page, perPage));
    }

    @GetMapping("/{id}")
    @RolesAdminUser
    public ResponseEntity<Order> getById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getById(id));
    }

    @GetMapping("/user/{id}")
    @RolesAdminUser
    public ResponseEntity<List<Order>> getByUserId(
            @PathVariable UUID id,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findByUserId(id, status, keyword));
    }

    @PutMapping("/feedback/{id}")
    @RolesAdminUser
    public ResponseEntity<?> FeedbackOrder(@PathVariable UUID id) {
        orderService.FeedbackOrder(id);
        return ResponseEntity.status(HttpStatus.OK).body("Updated successfully");
    }

    @PostMapping("")
    @RolesAdminUser
    public ResponseEntity<?> insert(
            @RequestBody @Valid OrderDTO orderDTO,
            HttpServletRequest request
    ) {
        var response = orderService.createOrder(orderDTO, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/return")
    @RolesAdminUser
    public ResponseEntity<?> orderReturn(HttpServletRequest request) {
        var response = orderService.orderReturn(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Order> update(@PathVariable UUID id, @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.update(id, orderDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        orderService.delete(orderService.getById(id));
        return ResponseEntity.status(HttpStatus.OK).body("Deleted successfully");
    }

    @PostMapping("/calculate")
    @RolesAdminUser
    public ResponseEntity<?> calculate(
            @RequestBody @Valid List<Cart> orderItems,
            @RequestParam(defaultValue = "0", required = false) double discount
    ) {
        var response = orderService.totalMoneyOrder(orderItems, discount);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/statistical")
    @RoleAdmin
    public ResponseEntity<?> statistical(@RequestParam(required = false) int year) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.statisticals(year));
    }

    @GetMapping("/export/{orderId}")
    @RoleAdmin
    public void export(@PathVariable UUID orderId, HttpServletResponse response) {
        Order order = orderService.getById(orderId);
        response.setContentType("application/pdf");
        String fileName = "bill_" + order.getCreatedAt().getTime() + ".pdf";
        response.setHeader("Content-Disposition", "filename=" + fileName);
        orderExportService.exportBillOrder(order, response);
    }

    @GetMapping("/filter")
    @RoleAdmin
    public ResponseEntity<?> filterOrders(
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "status", required = false) OrderStatus status,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "perPage", required = false) Integer perPage
    ) {
        Timestamp start = null;
        Timestamp end = null;
        if (startDate != null) {
            start = Timestamp.valueOf(startDate);
        }
        if (endDate != null) {
            end = Timestamp.valueOf(endDate);
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                orderService.getAllByFilter(start, end, status, page, perPage));
    }
}
