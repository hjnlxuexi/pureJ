package com.lamb.framework.adapter.protocol;

import com.lamb.framework.adapter.protocol.constant.AdapterConfConstants;
import com.lamb.framework.adapter.protocol.helper.AdapterConfigParser;
import com.lamb.framework.adapter.protocol.nettool.IProtocolTool;
import com.lamb.framework.base.Context;
import com.lamb.framework.base.Framework;
import com.lamb.framework.exception.ServiceRuntimeException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>Title : 外部服务适配器主类</p>
 * <p>Description : 执行调用外部服务</p>
 * <p>Date : 2017/4/16 10:17</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Component
public class ProtocolBaseAdapter {
    /**
     * 外部服务配置解析器
     */
    @Resource
    private AdapterConfigParser adapterConfigParser;
    /**
     * 调用外部服务
     * @param context 数据总线
     */
    public void execute(Context context){
        //1、解析服务配置
        Map config = adapterConfigParser.parseAdapterConf(context);
        Object net_tool = config.get(AdapterConfConstants.NET_TOOL_TAG);
        if (net_tool==null)//外部服务配置必须包含netTool节点
            throw new ServiceRuntimeException("5001" , ProtocolBaseAdapter.class);
        String netToolStr = net_tool.toString();
        //2、执行外部服务
        IProtocolTool netTool = (IProtocolTool)Framework.getBean(netToolStr);
        netTool.execute(context , config);
    }
}
