package com.demo.zk;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

public class ZkWatcherDemo {

    public static final String ZK_SERVICE = "localhost:2181";

    public static void main(String[] args) {
        ZkClient client = new ZkClient(ZK_SERVICE);
        client.setZkSerializer(new MyZkSerializer());
        client.subscribeDataChanges("/allen/a", new IZkDataListener() {


            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println("-----------收到节点变化了" + data + "-------------");
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                System.out.println("------------------收到节点删除了------------------");
            }

        });


        try {
            Thread.sleep(1000 * 60 * 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
