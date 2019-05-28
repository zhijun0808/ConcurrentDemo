package com.demo;

import com.demo.order.OrderService;
import com.demo.order.impl.OrderServiceImpWithDisLock;

import java.util.concurrent.CyclicBarrier;

public class ConcurrentTestDistributeDemo {


    public static void main(String[] args) {
        //并发数
        int currency = 50;

        CyclicBarrier cb = new CyclicBarrier(currency);


        for (int i = 0; i < currency; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //模拟分布式集群的场景
                    OrderService orderService = new OrderServiceImpWithDisLock();

                    System.out.println(Thread.currentThread().getName() + "---------------我准备好-----------");
                    //等待一起除发
                    try {
                        cb.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //调用创建订单服务
                    orderService.createOrder();
                }
            }).start();
        }


    }

}
