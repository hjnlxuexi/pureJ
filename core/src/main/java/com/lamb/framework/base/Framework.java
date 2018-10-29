package com.lamb.framework.base;

import com.lamb.framework.adapter.protocol.nettool.IProtocolTool;
import com.lamb.framework.service.op.BaseOP;
import com.lamb.framework.service.op.ReservedOP;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
     * @return 原子服务名称数组
     */
    public static Map<String,String[]> getBeanNames4OP(){
        String[] reservedList = springCtx.getBeanNamesForType(ReservedOP.class);
        String[] customList = springCtx.getBeanNamesForType(BaseOP.class);
        Map<String,String[]> opList = new HashMap<>();
        opList.put("reservedList", reservedList);
        opList.put("customList", customList);
        return opList;
    }

    /**
     * 获取适配器工具集合
     * @return 适配器工具beanName数组
     */
    public static String[] getAdapterNetTools(){
        return springCtx.getBeanNamesForType(IProtocolTool.class);
    }

    /**
     * 获取系统配置对象
     * @return 系统配置对象
     */
    public static Environment getEnvironment(){
        return springCtx.getEnvironment();
    }

    /**
     * 获取系统配置
     * @param key key
     * @return 属性值
     */
    public static String getProperty(String key){
        //获取环境变量
        Environment env = springCtx.getEnvironment();
        //返回属性值
        return env.getProperty(key);
    }
}
