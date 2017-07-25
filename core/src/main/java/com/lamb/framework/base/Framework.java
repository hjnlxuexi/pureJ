package com.lamb.framework.base;

import com.lamb.framework.service.OP;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * <p>Title : 框架类</p>
 * <p>Description : 获取容器中的bean实例</p>
 * <p>Date : 2017/2/28 17:24</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
public class Framework {

    /**
     * spring容器上下文
     */
    private static ApplicationContext springCtx;

    /**
     * 设置spring上下文
     * @param applicationContext spring上下文
     */
    public static void setSpringCtx(ApplicationContext applicationContext) {
        springCtx = applicationContext;
    }

    /**
     * 动态获取Spring上下文中的bean
     * @param beanId spring容器中的beanId
     * @return 返回bean实例
     */
    public static Object getBean(String beanId){
        return springCtx.getBean(beanId);
    }

    /**
     * 获取上下文中所有Service实例名称
     * @return service实例名称数组
     */
    public static String[] getBeanNames4Service(){
        return springCtx.getBeanNamesForAnnotation(Service.class);
    }

    /**
     * 获取上下文中所有原子服务类
     * @return 原子服务市里名称数组
     */
    public static String[] getBeanNames4OP(){
        return springCtx.getBeanNamesForType(OP.class);
    }
}
