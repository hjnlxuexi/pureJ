package com.lamb.discover.util;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 描述: zookeeper工具
 * 作者: hejie
 * 日期: 2018/6/28
 */
public class ZookeeperHelper {
    private static Logger logger = LoggerFactory.getLogger(ZookeeperHelper.class);

    /**
     * 节点书否存在
     *
     * @param path 节点路径
     * @param zk   实例
     * @return 是否
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static boolean isExists(String path, ZooKeeper zk) throws KeeperException, InterruptedException {
        Stat stat = zk.exists(path, true);
        return stat != null;
    }

    /**
     * 按序创建节点
     *
     * @param path 目标节点路径
     * @param zk   实例
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static void createPathRecursive(String path, ZooKeeper zk) throws KeeperException, InterruptedException {
        if ( isExists(path, zk) ) return;

        String[] subPathArray = path.split("/");
        String currentPath = "/";
        for (int i = 1; i < subPathArray.length; i++) {
            String s = subPathArray[i];
            if (s == null || s.equals("")) continue;
            //目标节点路径
            String targetPath = currentPath.endsWith("/") ? currentPath + s : currentPath + "/" + s;
            //创建临时节点，节点内容为空字符串
            if (!isExists(targetPath, zk)) {
                String path_ = zk.create(targetPath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                logger.debug("创建zk节点："+path_);
            }
            //记录当前路径
            currentPath = targetPath;
        }
    }

    /**
     * 获取本机IP
     *
     * @return 本机IP
     * @throws UnknownHostException
     */
    public static String getInetAddress() throws UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();
        return addr.getHostAddress();
    }
}
