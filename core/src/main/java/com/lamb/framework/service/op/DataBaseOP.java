package com.lamb.framework.service.op;

import com.lamb.framework.base.Context;
import com.lamb.framework.base.Framework;
import com.lamb.framework.channel.constant.ServicePacketConstants;
import com.lamb.framework.service.OP;
import com.lamb.framework.util.MyBatisMapperProxyUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述: 数据库原子服务
 * 作者: hejie
 * 日期: 2017/7/16
 */
@Service
public class DataBaseOP implements OP{

    private final static String SQL_JOIN_MARK = "\\+";
    /**
     * 原子数据库服务
     *
     * 注意：
     * 1、原子数据库服务参数定义必须为Map
     * 2、原子数据库服务的返回结果集，只能为：List、Map
     * 3、当返回结果集为List时，存入总线Context中的key为 'list'
     *
     * @param context 数据总线
     */
    @Override
    public void execute(Context context) {
        Map result = new HashMap();
        Object data;
        //1、提取参数
        Map params = context.getParams();
        params = params.isEmpty() ? new HashMap() : params;
        //2、分解sql
        String serviceId = context.getServiceId();
        String[] sqlArray = serviceId.split(SQL_JOIN_MARK);
        for (String s : sqlArray) {
            if(!s.contains("/")){
                //通过sql的id 执行数据库操作
                data = MyBatisMapperProxyUtil.executeSql(s , params);
            }else {
                //通过mapper，执行数据库操作
                String[] serviceArray = s.split("/");
                //3.1、执行sql，通过映射接口定义
                String serviceName = serviceArray[0];//服务BeanID
                String methodName = serviceArray[1];//服务方法
                Object proxy = Framework.getBean(serviceName);
                //3.2原子数据库服务，参数必须以map形式传入
                data = MyBatisMapperProxyUtil.invokeProxy(proxy, methodName, params, Map.class);
            }

            if(data==null)return;
            if (data instanceof List) {
                result.put(ServicePacketConstants.LIST, data);
            } else {
                result = (Map) data;
            }
            context.getParams().putAll(result);
        }
    }
}
