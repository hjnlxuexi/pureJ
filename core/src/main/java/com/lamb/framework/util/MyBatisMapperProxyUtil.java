package com.lamb.framework.util;

import com.lamb.framework.exception.ServiceRuntimeException;
import org.apache.ibatis.binding.MapperProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * <p>Title : 通过代理对象调用执行方法</p>
 * <p>Description : 通过代理对象调用目标方法</p>
 * <p>Date : 2017/3/9 20:19</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
public class MyBatisMapperProxyUtil {

    /**
     * 调用目标方法
     * @param proxy 代理对象
     * @param methodName 执行的方法名
     * @param params 参数
     * @param clazz 参数类型
     * @return 执行结果
     * @throws Throwable 获取目标对象失败
     */
    public static Object invokeProxy(Object proxy,String methodName,Object params,Class clazz){
        try {
            Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
            h.setAccessible(true);
            MapperProxy mapperProxy = (MapperProxy) h.get(proxy);
            Field advised = mapperProxy.getClass().getDeclaredField("mapperInterface");
            advised.setAccessible(true);
            Class target = (Class)advised.get(mapperProxy);
            Method method;
            Object[] args = {};
            if (params==null){
                method = target.getMethod(methodName);//获取无参方法
            }else {
                method = target.getMethod(methodName, clazz);//获取有参方法
                args = new Object[]{params};
            }
            return mapperProxy.invoke(proxy,method,args);
        } catch (Throwable throwable) {//mybatis代理执行错误
            throw new ServiceRuntimeException("3000" , MyBatisMapperProxyUtil.class , throwable);
        }
    }
}
