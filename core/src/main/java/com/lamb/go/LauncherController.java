package com.lamb.go;

import com.alibaba.fastjson.JSON;
import com.lamb.framework.adapter.protocol.constant.AdapterConfConstants;
import com.lamb.framework.adapter.protocol.helper.AdapterConfigParser;
import com.lamb.framework.base.Context;
import com.lamb.framework.base.Framework;
import com.lamb.framework.channel.builder.ICoreChannelBuilder;
import com.lamb.framework.channel.constant.ServiceConfConstants;
import com.lamb.framework.channel.constant.ServicePacketConstants;
import com.lamb.framework.channel.helper.ServiceConfigParser;
import com.lamb.framework.exception.ServiceRuntimeException;
import com.lamb.framework.service.flow.helper.FlowConfigParser;
import com.lamb.framework.service.flow.model.Flow;
import com.lamb.framework.service.flow.model.Forward;
import com.lamb.framework.service.flow.model.Step;
import com.lamb.framework.validator.converter.FieldConverter;
import com.lamb.framework.validator.converter.FieldConverterParser;
import lombok.Cleanup;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
public class LauncherController {
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
     * 响应报文组装器
     */
    @Resource
    private ICoreChannelBuilder coreDataBuilder;
    /**
     * 外部服务配置解析器
     */
    @Resource
    private AdapterConfigParser adapterConfigParser;
    /**
     * 字段转换器解析器
     */
    @Resource
    private FieldConverterParser fieldConverterParser;

    /**
     * 检查服务是否正常
     * @param context 总线
     * @return 消息
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/checkActive")
    public Map checkActive(@RequestBody Context context) {
        try {
            Map out = new HashMap();
            buildSuccess(out, new HashMap());
            return out;
        } catch (Exception e) {
            if (e instanceof ServiceRuntimeException)
                return coreDataBuilder.buildError((ServiceRuntimeException) e);
            return coreDataBuilder.buildError(new ServiceRuntimeException("4000", this.getClass(), e, "/checkActive"));
        }
    }

    /**
     * 加载服务配置
     *
     * @param context 数据总线
     * @return 返回服务配置对象
     */
    @RequestMapping("/loadServiceConf")
    public Map loadServiceConf(@RequestBody Context context) {
        try {
            Map packet = context.getServiceInput();
            Object _service = packet.get("service");
            if (_service == null) //请求报文头服务编码为空
                throw new ServiceRuntimeException("1002", this.getClass());
            String service = _service.toString();
            context.setServiceCode(service);
            Map out = new HashMap();
            Map body = serviceConfigParser.parseServiceConf(context);
            buildSuccess(out, body);
            return out;
        } catch (Exception e) {
            if (e instanceof ServiceRuntimeException)
                return coreDataBuilder.buildError((ServiceRuntimeException) e);
            return coreDataBuilder.buildError(new ServiceRuntimeException("4000", this.getClass(), e, "/loadServiceConf"));
        }
    }

    /**
     * 保存服务配置
     *
     * @param context 数据总线
     * @return 消息
     */
    @RequestMapping("/saveServiceConf")
    public Map saveServiceConf(@RequestBody Context context) {
        try {
            Map service = context.getServiceInput();
            StringBuilder sb = new StringBuilder();
            //1、组装服务配置：xml
            buildServiceConfig(service, sb);
            String serviceCode = service.get("code").toString();
            String filePath = Framework.getProperty("biz.conf.service") + serviceCode + ".xml";
            //2、保存服务配置文件
            this.saveFile(filePath,sb);

            Map out = new HashMap();
            buildSuccess(out, new HashMap<>());
            return out;
        } catch (Exception e) {
            if (e instanceof ServiceRuntimeException)
                return coreDataBuilder.buildError((ServiceRuntimeException) e);
            return coreDataBuilder.buildError(new ServiceRuntimeException("4000", this.getClass(), e, "/saveServiceConf"));
        }
    }

    /**
     * 加载流程服务配置
     *
     * @param context 数据总线
     * @return 流程服务配置
     */
    @RequestMapping("/loadFlowConf")
    public Map loadFlowConf(@RequestBody Context context) {
        try {
            Map packet = context.getServiceInput();
            Object _flowPath = packet.get("serviceFlowPath");
            if (_flowPath == null)
                throw new RuntimeException("缺少【serviceFlowPath】参数");
            String flow_path = _flowPath.toString();
            context.setServiceId(flow_path);
            Map out = new HashMap();
            Flow flow = flowConfigParser.parseFlowConfig(context);
            buildSuccess(out, flow);
            return out;
        } catch (Exception e) {
            if (e instanceof ServiceRuntimeException)
                return coreDataBuilder.buildError((ServiceRuntimeException) e);
            return coreDataBuilder.buildError(new ServiceRuntimeException("4000", this.getClass(), e, "/loadFlowConf"));
        }
    }

    /**
     * 保存流程配置
     * @param context 总线
     * @return 消息
     */
    @RequestMapping("/saveFlowConf")
    public Map saveFlowConf(@RequestBody Context context){
        try {
            Map flowMap = context.getServiceInput();
            Flow flow = JSON.parseObject(JSON.toJSONString(flowMap), Flow.class);
            StringBuilder sb = new StringBuilder();
            //1、组装xml
            buildFlowConfig(flow, sb);
            String serviceCode = flowMap.get("path").toString();
            String filePath = Framework.getProperty("biz.conf.flow") + serviceCode + ".xml";
            //2、保存文件
            this.saveFile(filePath,sb);

            Map out = new HashMap();
            buildSuccess(out, new HashMap<>());
            return out;
        } catch (Exception e) {
            if (e instanceof ServiceRuntimeException)
                return coreDataBuilder.buildError((ServiceRuntimeException) e);
            return coreDataBuilder.buildError(new ServiceRuntimeException("4000", this.getClass(), e, "/saveFlowConf"));
        }
    }

    /**
     * 加载原子服务列表
     *
     * @param context 数据总线
     * @return 所有原子服务
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/loadOPList")
    public Map loadOPList(@RequestBody Context context) {
        try {
            Map ops = Framework.getBeanNames4OP();
            Map out = new HashMap();
            buildSuccess(out, ops);
            return out;
        } catch (Exception e) {
            if (e instanceof ServiceRuntimeException)
                return coreDataBuilder.buildError((ServiceRuntimeException) e);
            return coreDataBuilder.buildError(new ServiceRuntimeException("4000", this.getClass(), e, "/loadOPList"));
        }
    }

    /**
     * 加载适配器默认配置
     * @param context 总线
     * @return 默认配置
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/loadAdapterDefaults")
    public Map loadAdapterDefaults(@RequestBody Context context) {
        try {
            Map body = new HashMap();
            String[] netTools = Framework.getAdapterNetTools();
            body.put("netTools", netTools);
            body.put(AdapterConfConstants.HOST_TAG , Framework.getProperty("adapter.host"));
            body.put(AdapterConfConstants.CONNECT_TIMEOUT_TAG, Framework.getProperty("adapter.connectTimeout"));
            body.put(AdapterConfConstants.RESPONSE_TIMEOUT_TAG , Framework.getProperty("adapter.responseTimeout"));
            body.put(AdapterConfConstants.CHARSET_TAG, Framework.getProperty("adapter.charset"));
            Map out = new HashMap();
            buildSuccess(out, body);
            return out;
        } catch (Exception e) {
            if (e instanceof ServiceRuntimeException)
                return coreDataBuilder.buildError((ServiceRuntimeException) e);
            return coreDataBuilder.buildError(new ServiceRuntimeException("4000", this.getClass(), e, "/loadAdapterDefaults"));
        }
    }

    /**
     * 加载适配器服务配置
     * @param context 总线
     * @return xml转json
     */
    @RequestMapping("/loadAdapterConf")
    public Map loadAdapterConf(@RequestBody Context context) {
        try {
            Map packet = context.getServiceInput();
            Object _adapter = packet.get("adapter");
            if (_adapter == null) //请求报文头服务编码为空
                throw new ServiceRuntimeException("1002", this.getClass());
            String adapter = _adapter.toString();
            context.setServiceId(adapter);
            Map out = new HashMap();
            Map body = adapterConfigParser.parseAdapterConf(context);
            buildSuccess(out, body);
            return out;
        } catch (Exception e) {
            if (e instanceof ServiceRuntimeException)
                return coreDataBuilder.buildError((ServiceRuntimeException) e);
            return coreDataBuilder.buildError(new ServiceRuntimeException("4000", this.getClass(), e, "/loadAdapterConf"));
        }
    }

    /**
     * 保存适配器配置
     * @param context 总线
     * @return 消息
     */
    @RequestMapping("/saveAdapterConf")
    public Map saveAdapterConf(@RequestBody Context context) {
        try {
            Map adapter = context.getServiceInput();
            StringBuilder sb = new StringBuilder();
            //1、组装xml
            buildAdapterConfig(adapter, sb);

            String adapterPath = adapter.get("path").toString();
            String filePath = Framework.getProperty("biz.conf.adapter") + adapterPath + ".xml";
            //2、保存配置文件
            this.saveFile(filePath,sb);

            Map out = new HashMap();
            buildSuccess(out, new HashMap<>());
            return out;
        } catch (Exception e) {
            if (e instanceof ServiceRuntimeException)
                return coreDataBuilder.buildError((ServiceRuntimeException) e);
            return coreDataBuilder.buildError(new ServiceRuntimeException("4000", this.getClass(), e, "/saveAdapterConf"));
        }
    }

    /**
     * 获取字段转换器
     * @param context 总线
     * @return 消息
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/loadConverters")
    public Map loadConverters(@RequestBody Context context) {
        try {
            List<FieldConverter> converters = fieldConverterParser.getConverters();
            Map body = new HashMap();
            body.put("converters", converters);
            Map out = new HashMap();
            buildSuccess(out, body);
            return out;
        } catch (Exception e) {
            if (e instanceof ServiceRuntimeException)
                return coreDataBuilder.buildError((ServiceRuntimeException) e);
            return coreDataBuilder.buildError(new ServiceRuntimeException("4000", this.getClass(), e, "/loadConverters"));
        }
    }

    /**
     * 保存字段转换器配置
     * @param context 总线
     * @return 消息
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/saveConverterConf")
    public Map saveConverterConf(@RequestBody Context context){
        try {
            Map data = context.getServiceInput();
            List<Map> converters = (List<Map>)data.get("converters");
            StringBuilder sb = new StringBuilder();
            //1、组装xml
            buildConverterConfig(converters, sb);
            String filePath = Framework.getProperty("biz.conf.converters") +".xml";
            //2、保存文件
            this.saveFile(filePath,sb);

            Map out = new HashMap();
            buildSuccess(out, new HashMap<>());
            return out;
        } catch (Exception e) {
            if (e instanceof ServiceRuntimeException)
                return coreDataBuilder.buildError((ServiceRuntimeException) e);
            return coreDataBuilder.buildError(new ServiceRuntimeException("4000", this.getClass(), e, "/saveFlowConf"));
        }
    }

    /**
     * 拼装转换器配置
     * @param converters json格式
     * @param sb xml格式
     */
    @SuppressWarnings("unchecked")
    private void buildConverterConfig(List<Map> converters, StringBuilder sb) {
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<converters>\n");
        for (Map converter : converters) {
            Object name = converter.get("name");
            if (name==null || name.equals("")){
                throw new ServiceRuntimeException("1012",this.getClass());
            }
            sb.append("    <").append(name).append(">\n");
            Map<String , String> rules = (Map<String , String>)converter.get("rules");
            for (String key : rules.keySet()) {
                String to = rules.get(key);
                sb.append("        <rule from=\"").append(key).append("\" to=\"").append(to).append("\"/>\n");
            }
            sb.append("    </").append(name).append(">\n");
        }
        sb.append("</converters>\n");
    }

    /**
     * 组装xml
     * @param adapter 适配器内容json
     * @param sb 拼装结果xml
     */
    private void buildAdapterConfig(Map adapter, StringBuilder sb) {
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<adapter>\n");
        String name = adapter.get("name")==null ? "" : adapter.get("name").toString();
        sb.append("    <name>").append(name).append("</name>\n");

        if (adapter.get("host") == null || adapter.get("host").toString().equals(""))
            throw new ServiceRuntimeException("5001", this.getClass());
        sb.append("    <host>").append(adapter.get("host")).append("</host>\n");

        if (adapter.get("netTool") == null || adapter.get("netTool").toString().equals(""))
            throw new ServiceRuntimeException("5001", this.getClass());
        sb.append("    <netTool>").append(adapter.get("netTool")).append("</netTool>\n");

        if (adapter.get("service") == null || adapter.get("service").toString().equals(""))
            throw new ServiceRuntimeException("5001", this.getClass());
        sb.append("    <service>").append(adapter.get("service")).append("</service>\n");

        Object connectTimeout = adapter.get("connectTimeout");
        if (connectTimeout!=null && !connectTimeout.toString().equals("")){
            sb.append("    <connectTimeout>").append(adapter.get("connectTimeout")).append("</connectTimeout>\n");
        }
        Object responseTimeout = adapter.get("responseTimeout");
        if (responseTimeout!=null && !responseTimeout.toString().equals("")){
            sb.append("    <responseTimeout>").append(adapter.get("responseTimeout")).append("</responseTimeout>\n");
        }
        Object charset = adapter.get("charset");
        if (charset!=null && !charset.toString().equals("")){
            sb.append("    <charset>").append(adapter.get("charset")).append("</charset>\n");
        }
        Object contentType = adapter.get("contentType");
        if (contentType!=null && !contentType.toString().equals("")){
            sb.append("    <contentType>").append(adapter.get("contentType")).append("</contentType>\n");
        }

        this.buildIOFields(adapter , sb);

        sb.append("</adapter>");
    }

    /**
     * 拼装Service配置文件内容
     *
     * @param service json格式
     * @param sb      xml格式
     */
    private void buildServiceConfig(Map service, StringBuilder sb) {
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<service>\n");
        sb.append("    <name>").append(service.get("name")).append("</name>\n");
        sb.append("    <desc>").append(service.get("desc")).append("</desc>\n");

        if (service.get("type") == null || service.get("type").toString().equals(""))
            throw new ServiceRuntimeException("1005", this.getClass());
        sb.append("    <type>").append(service.get("type")).append("</type>\n");

        if (service.get("id") == null || service.get("id").toString().equals(""))
            throw new ServiceRuntimeException("1005", this.getClass());
        sb.append("    <id>").append(service.get("id")).append("</id>\n");

        this.buildIOFields(service , sb);

        sb.append("</service>");
    }

    /**
     * 拼装服务配置input/output
     * @param data json格式
     * @param sb xml格式
     */
    @SuppressWarnings("unchecked")
    private void buildIOFields(Map data , StringBuilder sb){
        List<Map> output = (List) data.get(ServiceConfConstants.OUTPUT_TAG);
        List<Map> input = (List) data.get(ServiceConfConstants.INPUT_TAG);
        if (input == null || input.size() == 0) {
            sb.append("    <input/>\n");
        } else {
            sb.append("    <input>\n");
            buildField(input, sb, 2);
            sb.append("    </input>\n");
        }
        if (output == null || output.size() == 0) {
            sb.append("    <output/>\n");
        } else {
            sb.append("    <output>\n");
            buildField(output, sb, 2);
            sb.append("    </output>\n");
        }
    }

    /**
     * 拼装字段嵌套
     *
     * @param data  输入输出字段列表
     * @param sb    xml格式
     * @param level 缩进
     */
    @SuppressWarnings("unchecked")
    private void buildField(List<Map> data, StringBuilder sb, int level) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indent.append("    ");
        }
        for (Map field : data) {
            if (field.get("type") == null)
                throw new ServiceRuntimeException("1005", this.getClass());
            sb.append(indent).append("<field");
            if (field.get("name") != null && !field.get("name").toString().equals(""))
                sb.append(" name=\"").append(field.get("name")).append("\"");

            if (field.get("targetName") != null && !field.get("targetName").toString().equals(""))
                sb.append(" targetName=\"").append(field.get("targetName")).append("\"");

            if (field.get("converter") != null && !field.get("converter").toString().equals(""))
                sb.append(" converter=\"").append(field.get("converter")).append("\"");

            if (field.get("type") != null && !field.get("type").toString().equals(""))
                sb.append(" type=\"").append(field.get("type")).append("\"");

            if (field.get("regexp") != null && !field.get("regexp").toString().equals(""))
                sb.append(" regexp=\"").append(field.get("regexp")).append("\"");

            if (field.get("required") != null && !field.get("required").toString().equals(""))
                sb.append(" required=\"").append(field.get("required")).append("\"");

            if (field.get("desc") != null && !field.get("desc").toString().equals(""))
                sb.append(" desc=\"").append(field.get("desc")).append("\"");

            if (ServiceConfConstants.FIELD_TYPE_E.equals(field.get("type").toString())) {
                sb.append(">\n");
                buildField((List<Map>) field.get("children"), sb, ++level);
                sb.append(indent).append("</field>\n");
            } else {
                sb.append("/>\n");
            }
        }
    }

    /**
     * 拼装流程配置文件
     * @param flow 流程对象
     * @param sb xml格式
     */
    private void buildFlowConfig(Flow flow, StringBuilder sb) {
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        String title = flow.getTitle();
        sb.append("<flow title=\"").append(title==null?"":title).append("\">\n");
        Map<String, Step> steps = flow.getSteps();
        for (String s : steps.keySet()) {
            Step step = steps.get(s);
            String index = step.getIndex();
            String ref = step.getRef();
            String desc = step.getDesc();
            String next = step.getNext();
            List<Forward> mapping = step.getMapping();
            if (index==null||ref==null||"".equals(index)||"".equals(ref))
                throw new ServiceRuntimeException("2004",this.getClass());
            sb.append("    <step index=\"").append(index).append("\"")
                    .append(" ref=\"").append(ref).append("\"")
                    .append(" desc=\"").append(desc==null?"":desc).append("\"");
            if (next!=null && !next.equals("")){
                sb.append(" next=\"").append(next).append("\"");
            }
            if (mapping==null||mapping.size()==0){
                sb.append("/>\n");
            }else {
                sb.append(">\n");
                for (Forward forward : mapping) {
                    String condition = forward.getCondition();
                    String to = forward.getTo();
                    String mDesc = forward.getDesc();
                    if (condition==null||to==null||condition.equals("")||to.equals(""))
                        throw new ServiceRuntimeException("2004",this.getClass());
                    sb.append("        <forward condition=\"").append(condition).append("\"")
                            .append(" to=\"").append(to).append("\"")
                            .append(" desc=\"").append(mDesc==null?"":mDesc).append("\"")
                            .append("/>\n");
                }
                sb.append("    </step>\n");
            }
        }
        sb.append("</flow>");
    }

    /**
     * 保存文件
     * @param filePath 文件路径
     * @param sb 文件字符串
     * @throws IOException 异常
     */
    private void saveFile(String filePath, StringBuilder sb) throws IOException {
        @Cleanup FileOutputStream outSTr = null;
        @Cleanup BufferedOutputStream Buff = null;
        File file = new File(filePath);
        File path = file.getParentFile();
        path.mkdirs();

        outSTr = new FileOutputStream(file);
        Buff = new BufferedOutputStream(outSTr);
        Buff.write(sb.toString().getBytes());
        Buff.flush();
        Buff.close();
    }
    /**
     * 组装返回数据
     *
     * @param out  输出对象
     * @param body 输出数据
     */
    @SuppressWarnings("unchecked")
    private void buildSuccess(Map out, Object body) {
        Map header = new HashMap();
        header.put(ServicePacketConstants.STATUS, Framework.getProperty("channel.service.success.code"));//响应状态码
        header.put(ServicePacketConstants.MSG, "成功"); //响应消息
        //报文头
        out.put(ServicePacketConstants.HEADER, header);
        //报文体
        out.put(ServicePacketConstants.BODY, body);
    }
}
