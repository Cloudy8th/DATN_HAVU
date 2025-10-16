package com.project.hmartweb.application.services.order;

import com.project.hmartweb.domain.entities.Order;
import jakarta.servlet.http.HttpServletResponse;

public interface IOrderExportService {
    void exportBillOrder(Order order, HttpServletResponse response);
}
