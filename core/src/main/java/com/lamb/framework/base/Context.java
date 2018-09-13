package com.lamb.framework.base;

import com.lamb.framework.channel.constant.ServiceConfConstants;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title : 数据总线</p>
 * <p>Description : 服务流转中诗数据的唯一载体；
 * 1、服务输入参数；
 * 2、服务输出数据；
 * 3、外部服务请求报文；
 * 4、外部服务响应报文；
 * 5、动态数据区。
 * </p>
 * <p>Date : 2017/2/28 17:26</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Data
public class Context {
    /**
     * 服务输入参数
     */
    private Map serviceInput = new HashMap();
    /**
     * 服务输出数据
     */
    private Map serviceOutput = new HashMap();
    /**
     * 外部服务请求报文
     */
    private Object requestData = new Object();
    /**
     * 外部服务响应报文
     */
    private Object responseData = new Object();
    /**
     * 动态数据区
     */
    private Map params = new HashMap();
    /**
     * 服务编码，用于查找服务配置
     */
    private String serviceCode;
    /**
     * 服务ID，用于查找服务类-方法
     */
    private String serviceId;
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 是否过路服务，默认false
     */
    private boolean direct = false;
    /**
     * 过路交易类型，默认数据库交易
     */
    private String directType = ServiceConfConstants.DIRECT_TYPE_DB;
}
