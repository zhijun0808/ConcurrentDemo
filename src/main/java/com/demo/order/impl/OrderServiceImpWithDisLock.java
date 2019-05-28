package com.demo.order.impl;

import com.demo.order.OrderCodeGenerator;
import com.demo.order.OrderService;
import com.demo.zk.ZKDistributeImproveLock;

import java.util.concurrent.locks.Lock;

public class OrderServiceImpWithDisLock implements OrderService {

    private static OrderCodeGenerator ocg = new OrderCodeGenerator();

    @Override
    public void createOrder() {
        String orderCode = null;
        //分布式锁
        Lock lock = new ZKDistributeImproveLock("/allen666");
        try {
            lock.lock();
            orderCode = ocg.getOrderCode();
        } finally {
            lock.unlock();
        }


        System.out.println(Thread.currentThread().getName() + "****************" + orderCode);
    }
}
