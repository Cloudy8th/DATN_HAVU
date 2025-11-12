package com.project.tmartweb.web.controllers;

import com.project.tmartweb.application.responses.ProductSalesStatistical;
import com.project.tmartweb.application.responses.RevenueByDate;
import com.project.tmartweb.application.responses.RevenueByWeek;
import com.project.tmartweb.application.responses.Statistical;
import com.project.tmartweb.application.services.order.IOrderExportService;
import com.project.tmartweb.application.services.order.IOrderService;
import com.project.tmartweb.domain.dtos.OrderDTO;
import com.project.tmartweb.domain.entities.Cart;
import com.project.tmartweb.domain.entities.Order;
import com.project.tmartweb.domain.enums.OrderStatus;
import com.project.tmartweb.web.base.RestAPI;
import com.project.tmartweb.web.base.RoleAdmin;
import com.project.tmartweb.web.base.RolesAdminUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    private Timestamp parseTimestamp(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }

        // FIX 1: Replace '+' with ' ' for correct Timestamp conversion
        String cleanDateString = dateString.replace('+', ' ');

        // FIX 2: Check for end-of-day logic (dành cho endDate)
        // Nếu client truyền ngày-tháng-năm 00:00:00, ta muốn nó là 23:59:59 để bao gồm cả ngày đó.
        if (cleanDateString.endsWith("00:00:00")) {
            String datePart = cleanDateString.substring(0, cleanDateString.lastIndexOf("00:00:00"));
            return Timestamp.valueOf(datePart + "23:59:59");
        }

        return Timestamp.valueOf(cleanDateString);
    }

    @Autowired
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

    @GetMapping("/stats/daily")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RevenueByDate>> getDailyStats(
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate
    ) {
        Timestamp start = parseTimestamp(startDate);
        Timestamp end = parseTimestamp(endDate);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getDailyStats(start, end));
    }

    /**
     * Láº¥y thá»‘ng kÃª doanh thu theo tuáº§n trong má»™t khoáº£ng thá»i gian.
     */
    @GetMapping("/stats/weekly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RevenueByWeek>> getWeeklyStats(
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate
    ) {
        Timestamp start = parseTimestamp(startDate);
        Timestamp end = parseTimestamp(endDate);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getWeeklyStats(start, end));
    }

    /**
     * Láº¥y thá»‘ng kÃª doanh thu theo thÃ¡ng trong má»™t khoáº£ng thá»i gian.
     */
    @GetMapping("/stats/monthly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Statistical>> getMonthlyStats(
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate
    ) {
        Timestamp start = parseTimestamp(startDate);
        Timestamp end = parseTimestamp(endDate);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getMonthlyStats(start, end));
    }

    /**
     * Láº¥y thá»‘ng kÃª sá»‘ lÆ°á»£ng & doanh thu theo tá»«ng sáº£n pháº©m trong má»™t khoáº£ng thá»i gian.
     */
    @GetMapping("/stats/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductSalesStatistical>> getProductSalesStats(
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate
    ) {
        Timestamp start = parseTimestamp(startDate);
        Timestamp end = parseTimestamp(endDate);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getProductSalesStats(start, end));
    }

    @GetMapping("/export/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void export(@PathVariable UUID orderId, HttpServletResponse response) {
        Order order = orderService.getById(orderId);
        response.setContentType("application/pdf");
        String fileName = "bill_" + order.getCreatedAt().getTime() + ".pdf";
        response.setHeader("Content-Disposition", "filename=" + fileName);
        orderExportService.exportBillOrder(order, response);
    }

    @GetMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> filterOrders(
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "status", required = false) OrderStatus status,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "perPage", required = false) Integer perPage
    ) {
        Timestamp start = parseTimestamp(startDate);
        Timestamp end = parseTimestamp(endDate);

        if (start != null && end == null) {
            end = new Timestamp(System.currentTimeMillis());
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                orderService.getAllByFilter(start, end, status, page, perPage));
    }
}