package com.demo.order.impl;

import com.demo.order.OrderCodeGenerator;
import com.demo.order.OrderService;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OrderServiceImplWithLock  implements OrderService {

    private static OrderCodeGenerator ocg = new OrderCodeGenerator();

    private Lock lock = new ReentrantLock();


    @Override
    public void createOrder() {
        String orderCode = null;
        try{
            lock.lock();
            orderCode = ocg.getOrderCode();
        }finally {
            lock.unlock();
        }
        System.out.println(Thread.currentThread().getName() + "***********************>>" + orderCode);

    }
}
