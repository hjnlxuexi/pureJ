package com.lamb.framework.cache;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title : 服务配置缓存</p>
 * <p>Description : 缓存内部外部服务配置对象</p>
 * <p>Date : 2017/3/2 9:32</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Component
public class ConfigCache {
    /**
     * 缓存容器
     */
    private Map<String,Object> configMap = new HashMap<>();

    /**
     * 添加服务配置对象到缓存
     * 同步锁
     * @param code 服务配置对象键
     * @param config 服务配置对象
     */
    public synchronized void addConfig(String code , Object config){
        configMap.put(code , config);
    }

    /**
     * 获取配置对象
     * @param code 配置对象键
     * @return 返回配置对象
     */
    public Object getConfig(String code){
        return configMap.get(code);
    }

    /**
     * 是否包含配置对象
     * @param code 配置对象键
     * @return 是否包含
     */
    public boolean hasConfig(String code){
        return configMap.containsKey(code);
    }

    /**
     * 删除指定的配置对象
     * @param code 配置对象键
     */
    public void removeConfig(String code){
        configMap.remove(code);
    }

    /**
     * 清空缓存
     */
    public void clearCache(){
        configMap.clear();
    }

}
