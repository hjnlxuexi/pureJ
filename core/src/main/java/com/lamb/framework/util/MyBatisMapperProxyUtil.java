package com.lamb.framework.util;

import com.lamb.framework.base.Framework;
import com.lamb.framework.exception.ServiceRuntimeException;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * <p>Title : 通过代理对象调用执行方法</p>
 * <p>Description : 通过代理对象调用目标方法</p>
 * <p>Date : 2017/3/9 20:19</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
public class MyBatisMapperProxyUtil {
    private final static String SELECT = "SELECT";
    private final static String INSERT = "INSERT";
    private final static String UPDATE = "UPDATE";
    private final static String DELETE = "DELETE";

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

    /**
     * 不需要声明接口，直接映射mapper
     * @param sqlId 命名空间
     * @param params 参数
     * @return
     */
    public static Object invokeProxySimpled(String sqlId, Map params){
        // ！号结尾的namespace，为单条查询
        boolean selectOneFlag = sqlId.trim().endsWith("!");
        sqlId = selectOneFlag?sqlId.substring(0,sqlId.length()-1):sqlId;
        //获取session工厂类
        SqlSessionFactory sessionFactory = (SqlSessionFactory) Framework.getBean("sqlSessionFactory");
        //获取会话
        SqlSession session = sessionFactory.openSession();
        //获取sql语句
        String sql = sessionFactory.getConfiguration().getMappedStatement(sqlId).getBoundSql(null).getSql();

        if(sql.trim().toUpperCase().startsWith(INSERT)){
            session.insert(sqlId,params);
            return null;
        }else if(sql.trim().toUpperCase().startsWith(UPDATE)){
            session.update(sqlId,params);
            return null;
        }else if(sql.trim().toUpperCase().startsWith(DELETE)){
            session.delete(sqlId,params);
            return null;
        }else if(sql.trim().toUpperCase().startsWith(SELECT)){
            if (selectOneFlag){
                return session.selectOne(sqlId,params);
            }else {
                return session.selectList(sqlId,params);
            }
        }else {
            throw new ServiceRuntimeException("3001" , MyBatisMapperProxyUtil.class);
        }
    }
}
