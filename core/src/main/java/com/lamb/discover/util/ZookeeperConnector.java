package com.lamb.discover.util;

import com.lamb.framework.base.Framework;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 描述: zookeeper连接器
 * 作者: hejie
 * 日期: 2018/6/28
 */
@Component
public class ZookeeperConnector {

    /**
     * 获取Zookeeper实例
     * @return 返回Zookeeper实例
     * @throws IOException
     * @throws InterruptedException
     */
    public static ZooKeeper getInstance() throws IOException, InterruptedException {
        //--------------------------------------------------------------
        // 为避免连接还未完成就执行zookeeper的get/create/exists操作引起的（KeeperErrorCode = ConnectionLoss)
        // 这里等Zookeeper的连接完成才返回实例
        //--------------------------------------------------------------
        final CountDownLatch connectedSignal = new CountDownLatch(1);
        ZooKeeper zk = new ZooKeeper(Framework.getProperty("zk.urls"), Integer.valueOf(Framework.getProperty("zk.session.timeout")), new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if  (event.getState()  ==  Event.KeeperState.SyncConnected) {
                    connectedSignal.countDown();
                }
            }
        });
        connectedSignal.await();
        return zk;
    }
}
