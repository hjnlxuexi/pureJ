package com.lamb.framework.adapter.protocol.nettool;

import com.lamb.framework.adapter.protocol.builder.IBuilder;
import com.lamb.framework.adapter.protocol.connector.IConnector;
import com.lamb.framework.adapter.protocol.parser.IParser;
import com.lamb.framework.base.Context;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>Title : 外部服务为本框架核心服务</p>
 * <p>Description : 调用本框架服务</p>
 * <p>Date : 2017/4/16 11:05</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Component("core")
public class NetTool4Core implements INetTool {
    /**
     * 外部服务报文组装器
     */
    @Resource
    private IBuilder builder4Core;
    /**
     * 外部服务连接器
     */
    @Resource
    private IConnector connector4http;
    /**
     * 外部服务报文解析器
     */
    @Resource
    private IParser parser4Core;
    /**
     * 调用外部服务
     * @param context 数据总线
     * @param adapterConfig 适配器配置
     */
    @Override
    public void execute(Context context, Map adapterConfig) {
        //1、组装报文
        this.builder4Core.build(context , adapterConfig);
        //2、调用服务
        this.connector4http.connect(context , adapterConfig);
        //3、解析报文
        this.parser4Core.parse(context , adapterConfig);
    }
}
