package com.lamb.framework.adapter.protocol.builder;

import com.lamb.framework.base.Context;

import java.util.Map;

/**
 * <p>Title : 服务响应报文组装器</p>
 * <p>Description : 组装响应报文</p>
 * <p>Date : 2017/3/3 16:59</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
public interface IProtocolBuilder {
    /**
     * 组装响应报文
     * @param context 数据总线
     * @param adapterConfig 适配器配置
     */
    public void build(Context context  , Map adapterConfig);
}
