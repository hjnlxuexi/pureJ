package com.lamb.discover;

import com.lamb.discover.util.ZookeeperConnector;
import com.lamb.discover.util.ZookeeperHelper;
import com.lamb.framework.base.Framework;
import com.lamb.framework.exception.ServiceRuntimeException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 描述: 服务注册
 * 作者: hejie
 * 日期: 2018/6/28
 */
@Component
public class ServiceRegister {

    static final char REGISTRY_TYPE_ADD = 'C';
    static final char REGISTRY_TYPE_DEL = 'D';
    static final char REGISTRY_TYPE_UPD = 'U';

    /**
     * 注册服务信息
     * @param serviceCode 服务编码 : demo/demoDirectService
     * @param version 版本：时间戳
     * @param type C：新增，D：删除，U：修改
     */
    public static void register (String serviceCode, String version,char type){
        ZooKeeper zk;
        //服务提供者 IP地址
        String serviceIp = Framework.getProperty("service.registry.ip");
        //服务提供者 端口
        String servicePort = Framework.getProperty("server.port");
        //服务提供者 上下文
        String serviceContext = Framework.getProperty("server.context-path");
        try {
            //1、获取zk连接
            zk = ZookeeperConnector.getInstance();

            //2、创建服务节点，节点结构 /service/demo|demoDirectService/127.0.0.1:8080|pureJ : demo/demoDirectService
            serviceIp = serviceIp==null ? ZookeeperHelper.getInetAddress() : serviceIp;
            // /127.0.0.1:8080|pureJ
            String serverNode = "/"+serviceIp+":"+servicePort+serviceContext.replaceAll("/","|");
            //服务根节点 /service
            String serviceRootNode = "/service";
            //服务名称节点 /service/demo|demoDirectService
            String serviceNameNode = serviceRootNode+"/"+serviceCode.replaceAll("/","|");
            //服务提供节点 /service/demo_demoDirectService/127.0.0.1:8080_pureJ
            String serviceNode = serviceNameNode + serverNode;
            //服务节点内容 demo/demoDirectService
            String seerviceContent = serviceCode+"|"+version;

            switch (type){
                case 'C':
                    //创建服务根目录，持久节点
                    if (!ZookeeperHelper.isExists(serviceRootNode,zk))
                        zk.create(serviceRootNode, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    //创建服务名称节点，持久节点
                    if (!ZookeeperHelper.isExists(serviceNameNode,zk))
                        zk.create(serviceNameNode, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    //创建服务provider节点，临时节点，内容为：serviceCode|version
                    if (!ZookeeperHelper.isExists(serviceNode,zk))
                        zk.create(serviceNode, seerviceContent.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                    break;
                case 'D':
                    //删除服务
                    zk.delete(serviceNode,-1);
                    break;
                case 'U':
                    //更新服务
                    zk.setData(serviceNode, seerviceContent.getBytes(),-1);
                    break;
            }

        } catch (IOException | InterruptedException e) {
            throw new ServiceRuntimeException("6000" , ServiceRegister.class , e , "zookeeper");
        } catch (KeeperException e) {
            throw new ServiceRuntimeException("6001" , ServiceRegister.class , e , "zookeeper");
        }

    }
}
