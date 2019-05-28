package com.demo.order.impl;

import com.demo.order.OrderCodeGenerator;
import com.demo.order.OrderService;

public class OrderServiceImpl implements OrderService {

    private OrderCodeGenerator orderCodeGenerator = new OrderCodeGenerator();

    @Override
    public void createOrder() {

        String orderCode = orderCodeGenerator.getOrderCode();
        System.out.println(Thread.currentThread().getName() + "*******************" + orderCode);

    }
}
