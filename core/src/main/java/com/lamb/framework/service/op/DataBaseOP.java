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
        String serviceId = context.getServiceId();
        String[] serviceArray = serviceId.split("/");
        String serviceName = serviceArray[0];//服务BeanID
        String methodName = serviceArray[1];//服务方法
        Object proxy = Framework.getBean(serviceName);
        Map params = context.getParams();
        params = params.isEmpty() ? null : params;
        //原子数据库服务，参数必须以map形式传入
        Object data = MyBatisMapperProxyUtil.invokeProxy(proxy, methodName, params, Map.class);
        Map result = new HashMap();
        if (data instanceof List) {
            result.put(ServicePacketConstants.LIST, data);
        } else {
            result = (Map) data;
        }
        context.setParams(result);
    }
}
