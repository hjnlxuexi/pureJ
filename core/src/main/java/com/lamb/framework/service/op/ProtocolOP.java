package com.lamb.framework.service.op;

import com.lamb.framework.adapter.protocol.ProtocolBaseAdapter;
import com.lamb.framework.base.Context;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 描述: 外部原子服务，通过网络协议请求外部服务数据
 * 作者: hejie
 * 日期: 2017/7/16
 */
@Service
public class ProtocolOP implements ReservedOP {
    /**
     * 外部服务调用适配器
     */
    @Resource
    private ProtocolBaseAdapter protocolBaseAdapter;

    /**
     * 通过适配器调用外部服务
     *
     * @param context 数据总线
     */
    @Override
    public void execute(Context context) {
        protocolBaseAdapter.execute(context);
    }
}
