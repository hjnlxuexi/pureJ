package com.lamb.framework.channel.builder;

import com.lamb.framework.base.Context;
import com.lamb.framework.exception.ServiceRuntimeException;

import java.util.Map;

/**
 * <p>Title : 服务响应报文组装器</p>
 * <p>Description : 组装响应报文</p>
 * <p>Date : 2017/3/3 16:59</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
public interface ICoreChannelBuilder{
    /**
     * 组装响应报文
     * @param context 数据总线
     */
    public void build(Context context);

    /**
     * 组装服务失败的响应报文
     * @param e 异常对象
     * @return 返回响应报文
     */
    public Map buildError(ServiceRuntimeException e);
}
