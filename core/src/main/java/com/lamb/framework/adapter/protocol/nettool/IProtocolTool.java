package com.lamb.framework.adapter.protocol.nettool;

import com.lamb.framework.base.Context;

import java.util.Map;

/**
 * <p>Title : 外部服务通讯</p>
 * <p>Description : 定义外部服务通讯的服务接口</p>
 * <p>Date : 2017/2/28 22:19</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
public interface IProtocolTool {
    /**
     * 请求外部服务
     * @param context 数据总线
     */
    public void execute(Context context , Map adapterConfig);
}
