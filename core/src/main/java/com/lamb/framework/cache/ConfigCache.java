package com.lamb.framework.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Title : 服务配置缓存</p>
 * <p>Description : 缓存内部外部服务配置对象</p>
 * <p>Date : 2017/3/2 9:32</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
public class ConfigCache {
    /**
     * 缓存容器
     */
    private final static Map<String,Object> configMap = new ConcurrentHashMap<>();

    /**
     * 添加服务配置对象到缓存
     * 同步锁
     * @param code 服务配置对象键
     * @param config 服务配置对象
     */
    public static void addConfig(String code , Object config){
        configMap.put(code , config);
    }

    /**
     * 获取配置对象
     * @param code 配置对象键
     * @return 返回配置对象
     */
    public static Object getConfig(String code){
        return configMap.get(code);
    }

    /**
     * 是否包含配置对象
     * @param code 配置对象键
     * @return 是否包含
     */
    public static boolean hasConfig(String code){
        return configMap.containsKey(code);
    }

    /**
     * 删除指定的配置对象
     * @param code 配置对象键
     */
    public static void removeConfig(String code){
        configMap.remove(code);
    }

    /**
     * 清空缓存
     */
    public static void clearCache(){
        configMap.clear();
    }

}
