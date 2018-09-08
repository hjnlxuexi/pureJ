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
     * 多条sql连接符
     */
    private final static String SQL_JOIN_MARK = "\\+";
    /**
     * 查询单条数据模式
     */
    private final static String SELECT_ONE_PATTERN = "#ONE";
    /**
     * 原子数据库服务
     *
     * 注意：
     * 1、原子数据库服务参数定义必须为Map
     * 2、原子数据库服务的返回结果集，只能为：List、Map
     * 3、当返回结果集为List时，存入总线Context中的key为 'list'
     * 4、当数据库操作为查询，且明确查询结果为一条数据，则在sql-id后面拼接：#ONE
     *
     * @param context 数据总线
     */
    @Override
    @SuppressWarnings("unchecked")
    public void execute(Context context) {
        Map<String,Object> result = new HashMap<>();
        Object data;
        //1、提取参数
        Map params = context.getParams();
        params = params.isEmpty() ? new HashMap() : params;
        //2、分解sql
        String serviceId = context.getServiceId();
        String[] sqlArray = serviceId.split(SQL_JOIN_MARK);
        for (String sql : sqlArray) {
            //查询单条数据
            boolean isSelectOne = false;
            if (sql.endsWith(SELECT_ONE_PATTERN)){
                isSelectOne = true;
                sql = sql.substring(0,sql.indexOf(SELECT_ONE_PATTERN));
            }
            //   xxxx/yyyy  形式为面向接口的mapper访问数据库
            //   xxxx.yyyy  形式为通过namespace.sql，执行sql语句
            if(!sql.contains("/")){
                //通过sql的id 执行数据库操作
                data = MyBatisMapperProxyUtil.executeSql(sql , params);
            }else {
                //通过mapper，执行数据库操作
                String[] serviceArray = sql.split("/");
                //3.1、执行sql，通过映射接口定义
                String serviceName = serviceArray[0];//服务BeanID
                String methodName = serviceArray[1];//服务方法
                Object proxy = Framework.getBean(serviceName);
                //3.2原子数据库服务，参数必须以map形式传入
                data = MyBatisMapperProxyUtil.invokeProxy(proxy, methodName, params, Map.class);
            }

            if(data==null)continue;

            if (data instanceof List) {
                //查询单条数据
                if (isSelectOne){
                    List<Map<String,Object>> list = (List<Map<String,Object>>)data;
                    if (list.size()>0)result.putAll(list.get(0));
                }else {
                    //查询列表
                    result.put(ServicePacketConstants.LIST, data);
                }
            } else {
                result = (Map<String,Object>) data;
            }
            context.getParams().putAll(result);
        }
    }
}
