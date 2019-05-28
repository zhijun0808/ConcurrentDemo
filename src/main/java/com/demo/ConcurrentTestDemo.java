package com.demo;

import com.demo.order.OrderService;
import com.demo.order.impl.OrderServiceImplWithLock;

import java.util.concurrent.CyclicBarrier;

public class ConcurrentTestDemo {

    public static void main(String[] args) {

        int currency = 20; //并发数

        CyclicBarrier cb = new CyclicBarrier(currency);//并发栅栏，通过此类模拟并发情况

        OrderService orderService = new OrderServiceImplWithLock();

        for (int i = 0; i < currency; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "---------------我准备好-----------");

                try {
                    cb.await();//达到并发数后一起请求
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //调用创建订单服务
                orderService.createOrder();
            }).start();
        }


    }

}
