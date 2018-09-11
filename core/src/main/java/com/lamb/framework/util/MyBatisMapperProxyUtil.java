package com.lamb.framework.util;

import com.lamb.framework.base.Framework;
import com.lamb.framework.exception.ServiceRuntimeException;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.mybatis.spring.SqlSessionTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

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
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    public static Object invokeProxy(Object proxy,String methodName,Object params){
        try {
            Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
            h.setAccessible(true);
            MapperProxy mapperProxy = (MapperProxy) h.get(proxy);
            Field advised = mapperProxy.getClass().getDeclaredField("mapperInterface");
            advised.setAccessible(true);
            Class target = (Class)advised.get(mapperProxy);
            Object[] args = {};
            Method method = null;
            Method[] methods = target.getMethods();
            for (Method method1 : methods) {
                if (methodName.equals(method1.getName())) {
                    method = method1;
                    break;
                }
            }
            if (method==null){
                throw new ServiceRuntimeException("3002",MyBatisMapperProxyUtil.class,methodName);
            }
            int paraCnt = method.getParameterCount();
            if (paraCnt>0) args = new Object[]{params};
            return mapperProxy.invoke(proxy,method,args);
        } catch (Throwable throwable) {//mybatis代理执行错误
            throw new ServiceRuntimeException("3000" , MyBatisMapperProxyUtil.class , throwable);
        }
    }

    /**
     * 跳过mybatis接口映射直接执行sql
     * @param sqlId 定位sql
     * @param params 语句中的参数
     * @return sql执行返回结果
     */
    public static Object executeSql(String sqlId,Object params){
        SqlSessionTemplate sqlSessionTemplate = (SqlSessionTemplate)Framework.getBean("sqlSessionTemplate");
        MappedStatement statement = sqlSessionTemplate.getConfiguration().getMappedStatement(sqlId);
        //sql类型:UNKNOWN, INSERT, UPDATE, DELETE, SELECT, FLUSH
        SqlCommandType type = statement.getSqlCommandType();
        List<ResultMap> resultMaps = statement.getResultMaps();
        ResultMap rm = resultMaps!=null&&resultMaps.size()>0 ? resultMaps.get(0) : null;
        //获取结果类型，java.util.Map、java.util.HashMap
        String resultType = rm!=null&&rm.getType()!=null ? rm.getType().getName() : null;
        //执行sql
        if ( !type.equals(SqlCommandType.SELECT) ){
            //insert、update、delete
            return sqlSessionTemplate.update(sqlId , params);
        }
        if (resultType == null) throw new ServiceRuntimeException("3001" , MyBatisMapperProxyUtil.class , "resultType");
        //查询
        return sqlSessionTemplate.selectList(sqlId , params);
    }
}
