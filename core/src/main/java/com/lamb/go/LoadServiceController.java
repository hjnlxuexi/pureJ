package com.lamb.go;

import com.lamb.framework.base.Context;
import com.lamb.framework.base.Framework;
import com.lamb.framework.channel.helper.ServiceConfigParser;
import com.lamb.framework.service.flow.helper.FlowConfigParser;
import com.lamb.framework.service.flow.model.Flow;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title : 加载服务信息</p>
 * <p>Description : 获取服务配置</p>
 * <p>Date : 2017/4/19 22:41</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@RestController
public class LoadServiceController {
    /**
     * 服务配置解析器
     */
    @Resource
    private ServiceConfigParser serviceConfigParser;
    /**
     * 流程服务配置解析器
     */
    @Resource
    private FlowConfigParser flowConfigParser;

    /**
     * 加载服务配置
     * @param context 数据总线
     * @return 返回服务配置对象
     */
    @RequestMapping("/loadServiceConf")
    public Map loadServiceConf(@RequestBody Context context){
        try {
            Map packet = context.getServiceInput();
            Object _service = packet.get("service");
            if (_service==null) //请求报文头服务编码为空
                throw new RuntimeException("缺少【service】参数");
            String service = _service.toString();
            context.setServiceCode(service);
            return serviceConfigParser.parseServiceConf(context);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 加载流程服务配置
     * @param context 数据总线
     * @return 流程服务配置
     */
    @RequestMapping("/loadFlowConf")
    public Flow loadFlowConf(@RequestBody Context context){
        try {
            Map packet = context.getServiceInput();
            Object _flowPath = packet.get("serviceFlowPath");
            if (_flowPath==null)
                throw new RuntimeException("缺少【serviceFlowPath】参数");
            String flow_path = _flowPath.toString();
            context.setServiceId(flow_path);
            return flowConfigParser.parseFlowConfig(context);
        }catch (Exception e){
            e.printStackTrace();
            return new Flow();
        }
    }

    /**
     * 加载原子服务列表
     * @param context 数据总线
     * @return 所有原子服务
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/loadOPList")
    public Map loadOPList(@RequestBody Context context){
        try {
            String[] ops = Framework.getBeanNames4OP();
            Map data = new HashMap();
            data.put("opList" , ops);
            return data;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
