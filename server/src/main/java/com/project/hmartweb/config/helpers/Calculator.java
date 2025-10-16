package com.project.hmartweb.config.helpers;


import com.project.hmartweb.domain.entities.OrderDetail;

import java.util.List;

public class Calculator {
    public static double totalMoney(double price, int quantity) {
        return price * quantity;
    }

    public static double totalMoneyOrder(List<OrderDetail> orderDetails, double discount) {
        double total = 0;
        for (OrderDetail orderDetail : orderDetails) {
            total += orderDetail.getTotalMoney();
        }
        return total * (100 - discount) / 100;
    }
}
