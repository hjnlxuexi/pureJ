package com.lamb.api.discover;

import com.lamb.discover.util.ZookeeperConnector;
import com.lamb.framework.exception.ServiceRuntimeException;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述: 加载被发现的服务
 * 作者: hejie
 * 日期: 2018/6/30
 */
@Component
public class LoadService4Discover {

    /**
     * zk连接对象
     */
    @Resource
    private ZookeeperConnector zkConn;

    public List<Map<String,String>> load(){
        List<Map<String,String>> serviceList = new ArrayList<Map<String,String>>();

        ZooKeeper zk;
        try {
            //1、获取zk连接
            zk = zkConn.getZk();
            //2、获取服务名称列表，服务注册目录结构：/service/serviceName/server
            String rootNode = "/service";
            List<String> serviceNameList = zk.getChildren(rootNode,true);
            for (String service : serviceNameList) {
                String serviceNode = rootNode+"/"+service;
                //服务节点列表
                List<String> serverList = zk.getChildren(serviceNode,true,new Stat());
                for (String server : serverList) {
                    String serverNode = serviceNode+"/"+server;
                    Map<String,String> serviceInfo = new HashMap<String,String>();
                    String serviceCode = new String(zk.getData(serverNode,true,new Stat()));
                    //服务节点名称
                    serviceInfo.put("serviceNode",service);
                    //服务提供者路径
                    serviceInfo.put("serverPath",server.replaceAll("\\|","/"));
                    //服务编码
                    serviceInfo.put("serviceCode",serviceCode);

                    serviceList.add(serviceInfo);
                }
            }

        } catch (IOException | InterruptedException e) {
            throw new ServiceRuntimeException("6000" , this.getClass() , e , "zookeeper");
        } catch (KeeperException e) {
            throw new ServiceRuntimeException("6001" , this.getClass() , e , "zookeeper");
        }

        return serviceList;
    }
}
