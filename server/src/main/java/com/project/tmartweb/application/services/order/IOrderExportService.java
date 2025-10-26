package com.project.tmartweb.application.services.order;

import com.project.tmartweb.domain.entities.Order;
import jakarta.servlet.http.HttpServletResponse;

public interface IOrderExportService {
    void exportBillOrder(Order order, HttpServletResponse response);
}
