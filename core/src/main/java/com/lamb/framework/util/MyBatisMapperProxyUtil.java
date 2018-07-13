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
     * @param clazz 参数类型
     * @return 执行结果
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
            sqlSessionTemplate.update(sqlId , params);
            return null;
        }
        if (resultType == null) throw new ServiceRuntimeException("3001" , MyBatisMapperProxyUtil.class , "resultType");
        //查询
        if (resultType.equals("java.util.Map") || resultType.equals("java.util.HashMap")){
            List list = sqlSessionTemplate.selectList(sqlId , params);
            return list==null||list.size()==0 ? null
                    : list.size()==1 ? list.get(0)
                    : list;
        }

        throw new ServiceRuntimeException("3001" , MyBatisMapperProxyUtil.class , "resultType应配置为Map");
    }
}
