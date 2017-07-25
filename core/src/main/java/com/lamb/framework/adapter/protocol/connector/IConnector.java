package com.lamb.framework.adapter.protocol.connector;

import com.lamb.framework.base.Context;

import java.util.Map;

/**
 * <p>Title : 通讯连接接口</p>
 * <p>Description : 用于通讯连接统一规范</p>
 * <p>Date : 2017/2/28 22:01</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
public interface IConnector {
    /**
     * 与外部服务进行连接，并将响应数据放入数据总线
     * @param context 数据总线
     * @param adapterConfig  外部服务配置对象
     */
    public void connect(Context context  , Map adapterConfig);
}
