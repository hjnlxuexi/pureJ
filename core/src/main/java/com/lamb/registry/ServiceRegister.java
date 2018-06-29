package com.lamb.registry;

import com.lamb.framework.exception.ServiceRuntimeException;
import com.lamb.registry.util.ZookeeperHelper;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 描述: 服务注册
 * 作者: hejie
 * 日期: 2018/6/28
 */
@Component
public class ServiceRegister {
    /**
     * 服务IP
     */
    @Value("${service.registry.ip}")
    private String serviceIp;
    /**
     * 服务端口
     */
    @Value("${server.port}")
    private String servicePort;
    /**
     * 服务上下文
     */
    @Value("${server.context-path}")
    private String serviceContext;
    /**
     * zk连接对象
     */
    @Resource
    private ZookeeperConnector zkConn;

    public static final char REGISTRY_TYPE_ADD = 'C';
    public static final char REGISTRY_TYPE_DEL = 'D';
    public static final char REGISTRY_TYPE_UPD = 'U';

    /**
     * 注册服务信息
     * @param serviceCode 服务编码 : demo/demoDirectService
     * @param version 版本：时间戳
     * @param type C：新增，D：删除，U：修改
     */
    public void register (String serviceCode, String version,char type){
        ZooKeeper zk;
        try {
            //1、获取zk连接
            zk = zkConn.getZk();

            //2、创建服务节点，节点结构 /demo_demoDirectService/127.0.0.1:8080_pureJ : demo/demoDirectService
            serviceIp = serviceIp==null ? ZookeeperHelper.getInetAddress() : serviceIp;
            // /127.0.0.1:8080_pureJ
            String serverNode = "/"+serviceIp+":"+servicePort+serviceContext.replaceAll("/","_");
            // /demo_demoDirectService
            String serviceRootNode = "/"+serviceCode.replaceAll("/","_");
            // /demo_demoDirectService/127.0.0.1:8080_pureJ
            String serviceNode = serviceRootNode + serverNode;
            // demo/demoDirectService
            String seerviceContent = serviceCode+"|"+version;

            switch (type){
                case 'C':
                    //创建服务根节点，持久节点
                    if (!ZookeeperHelper.isExists(serviceRootNode,zk))
                        zk.create(serviceRootNode, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
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
            throw new ServiceRuntimeException("6000" , this.getClass() , e , "zookeeper");
        } catch (KeeperException e) {
            throw new ServiceRuntimeException("6001" , this.getClass() , e , "zookeeper");
        }

    }
}
