package com.demo.zk;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/****
 * 此类实现 会引起 惊群效应 ZKDistributeImproveLock 是改进后的方式
 */
public class ZKDistributeLock implements Lock {

    public static final String ZK_SERVICE = "localhost:2181";

    private String lockPath;

    private ZkClient client;

    public ZKDistributeLock(String lockPath) {
        super();
        this.lockPath = lockPath;
        client = new ZkClient(ZK_SERVICE);
        client.setZkSerializer(new MyZkSerializer());
    }


    @Override
    public void lock() {//如果获取不到，阻塞等待
        if (!tryLock()) {
            waitForLock();//没获到锁，阻塞自己
            lock();//再次尝试
        }
    }

    private void waitForLock() {
        CountDownLatch cdl = new CountDownLatch(1);
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataDeleted(String dataPaht) throws Exception {
                System.out.println("-----------------收到节点被删除----------------");
                cdl.countDown();
            }

            @Override
            public void handleDataChange(String dataPaht, Object data) {

            }
        };

        client.subscribeDataChanges(lockPath, listener);
        if (this.client.exists(lockPath)) {
            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //取消注册
        client.unsubscribeDataChanges(lockPath, listener);

    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        //创建节点 创建节点
        try {
            client.createEphemeral(lockPath);
        } catch (ZkNodeExistsException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        client.delete(lockPath);
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
