package com.lamb.framework.base;

import com.lamb.framework.channel.constant.ServiceConfConstants;

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
    private Map requestData = new HashMap();
    /**
     * 外部服务响应报文
     */
    private Map responseData = new HashMap();
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

    public Map getServiceInput() {
        return serviceInput;
    }

    public void setServiceInput(Map serviceInput) {
        this.serviceInput = serviceInput;
    }

    public Map getServiceOutput() {
        return serviceOutput;
    }

    public void setServiceOutput(Map serviceOutput) {
        this.serviceOutput = serviceOutput;
    }

    public Map getRequestData() {
        return requestData;
    }

    public void setRequestData(Map requestData) {
        this.requestData = requestData;
    }

    public Map getResponseData() {
        return responseData;
    }

    public void setResponseData(Map responseData) {
        this.responseData = responseData;
    }

    public Map getParams() {
        return params;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public boolean isDirect() {
        return direct;
    }

    public void setDirect(boolean direct) {
        this.direct = direct;
    }

    public String getDirectType() {
        return directType;
    }

    public void setDirectType(String directType) {
        this.directType = directType;
    }
}
