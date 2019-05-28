package com.demo.zk;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/***
 * 此类是 ZKDistributeLock 类的改进，根据zookeeper的有序节点的创建，当前抢到锁的节点寻找
 */
public class ZKDistributeImproveLock implements Lock {

    public static final String ZK_SERVICE = "localhost:2181";


    private String lockPath;

    private ZkClient client;

    private String currentPath;

    private String beforePath;


    public ZKDistributeImproveLock(String lockPath) {
        super();
        this.lockPath = lockPath;
        client = new ZkClient(ZK_SERVICE);
        client.setZkSerializer(new MyZkSerializer());
        if (!this.client.exists(lockPath)) {
            try {
                this.client.createPersistent(lockPath);//创建节点
            } catch (ZkNodeExistsException e) {
            }
        }
    }


    /***
     * 没有抢到锁，自己阻塞,
     * 直到订单服务中调用unlock方法释放掉锁,
     * 会通过handleDataDeleted 监听唤醒阻塞,
     * 继续执行下面递归
     */
    @Override
    public void lock() {
        if (!tryLock()) {//尝试获取锁
            waitForLock();
            lock();
        }
    }

    private void waitForLock() {
        CountDownLatch cdl = new CountDownLatch(1);
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataDeleted(String dataPaht) throws Exception {
                System.out.println("-----------------收到节点被删除----------------");
                cdl.countDown();//释放阻塞
            }

            @Override
            public void handleDataChange(String dataPaht, Object data) {

            }
        };

        client.subscribeDataChanges(beforePath, listener);//监听节点删除状态
        if (this.client.exists(beforePath)) {
            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //取消注册
        client.unsubscribeDataChanges(beforePath, listener);

    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    /***
     *  同一时刻尝试获取锁的方法，只会有currentPath当前节点与所有节点中做小的一个匹配
     *  不匹配的当前节点后面一个结点则会被监听并且会阻塞，直到当前节点被释放后其他结点继续强锁
     * @return
     */
    @Override
    public boolean tryLock() {
        if (this.currentPath == null) {//当前节点为空，则创建临时有序节点
            currentPath = this.client.createEphemeralSequential(lockPath + "/", "aaa");
        }
        //获取所有的子节点
        List<String> children = this.client.getChildren(lockPath);
        Collections.sort(children);//排序节点
        if (currentPath.equals(lockPath + "/" + children.get(0))) {//判断是否第一个节点
            return true;
        } else {
            //渠道前一个
            //得到字节的索引号
            int curIndex = children.indexOf(currentPath.substring(lockPath.length() + 1));
            beforePath = lockPath + "/" + children.get(curIndex - 1);
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        client.delete(currentPath);
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
