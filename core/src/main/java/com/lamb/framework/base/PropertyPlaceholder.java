package com.lamb.framework.base;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * <p>Title : 改在属性文件配置</p>
 * <p>Description : 提供静态方法获取属性配置，避免定义过多成员属性</p>
 * <p>Date : 2017/4/13 20:07</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
public class PropertyPlaceholder extends PropertyPlaceholderConfigurer {
    private static Map<String, String> propertyMap;

    /**
     * 重写属性处理方法
     * @param beanFactoryToProcess bean工厂
     * @param props 属性对象
     * @throws BeansException bean异常
     */
    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
            throws BeansException {
        super.processProperties(beanFactoryToProcess, props);
        propertyMap = new HashMap<>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            propertyMap.put(keyStr, value);
        }
    }

    /**
     * 以静态方法的形式访问属性值
     * @param name 属性名称
     * @return 返回属性值
     */
    public static String getProperty(String name) {
        return propertyMap.get(name);
    }
}
