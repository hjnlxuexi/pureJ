package com.lamb.framework.channel.builder;

import com.lamb.framework.base.Context;
import com.lamb.framework.base.Framework;
import com.lamb.framework.channel.constant.ServiceConfConstants;
import com.lamb.framework.channel.constant.ServicePacketConstants;
import com.lamb.framework.channel.helper.ServiceConfigParser;
import com.lamb.framework.exception.ServiceRuntimeException;
import com.lamb.framework.validator.ConfigValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title : 服务响应报文组装器</p>
 * <p>Description : 组装响应报文</p>
 * <p>Date : 2017/3/3 16:50</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Slf4j
@Component
public class CoreChannelBuilder implements ICoreChannelBuilder {
    /**
     * 服务配置解析器
     */
    @Resource
    private ServiceConfigParser serviceConfigParser;

    /**
     * 组装响应报文
     *
     * @param context 数据总线
     */
    @Override
    public void build(Context context) {
        log.debug("组装服务响应报文，开始...");
        long start = System.currentTimeMillis();
        //1、获取服务配置对象
        Map config = serviceConfigParser.parseServiceConf(context);

        //2、组装报文头
        this.buildHeader(context);

        //3、组装报文体
        this.buildBody(config, context);

        long end = System.currentTimeMillis();
        log.debug("组装服务响应报文，结束【" + (end - start) + "毫秒】");
    }

    /**
     * 组装报文头
     *
     * @param context 数据总线
     */
    @SuppressWarnings("unchecked")
    private void buildHeader(Context context) {
        Map header = new HashMap();
        header.put(ServicePacketConstants.STATUS, Framework.getProperty("channel.service.success.code"));//响应状态
        header.put(ServicePacketConstants.MSG, Framework.getProperty("channel.service.success.msg")); //响应消息
        context.getServiceOutput().put(ServicePacketConstants.HEADER, header);
    }

    /**
     * 组装报文体
     *
     * @param config  服务配置对象
     * @param context 数据总线
     */
    @SuppressWarnings("unchecked")
    private void buildBody(Map config, Context context) {
        Map body = new HashMap();
        //1、服务配置的输出域列表
        List<Map> outputList = (List<Map>) config.get(ServiceConfConstants.OUTPUT_TAG);
        //2、待处理的数据
        Map data = context.getParams();
        //默认不过滤和验证输出数据
        if ( !Boolean.valueOf(Framework.getProperty("channel.validate.output")) ){
            context.getServiceOutput().put(ServicePacketConstants.BODY, data);
            return;
        }
        //3、遍历输出域列表
        for (Map field : outputList) {
            if (field.get(ServiceConfConstants.NAME_PROP) == null) //服务配置中必须存在字段名称
                throw new ServiceRuntimeException("1005" , this.getClass());
            String name = field.get(ServiceConfConstants.NAME_PROP).toString();
            Object value = data.get(name);
            //4、验证字段值
            value = ConfigValidator.validateField(value, field);
            //5、将字段键值对放入总线
            Object target_name = field.get(ServiceConfConstants.TARGET_NAME_PROP);
            if (target_name!=null&&!target_name.toString().isEmpty()) //name --> targetName
                name = target_name.toString();
            body.put(name, value);
        }
        context.getServiceOutput().put(ServicePacketConstants.BODY, body);
    }

    /**
     * 组装服务失败的响应报文
     *
     * @param e 异常对象
     * @return 返回响应报文
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map buildError(ServiceRuntimeException e) {
        Map errorResult = new HashMap();
        String key = e.getMessageKey();
        //解析后的消息内容
        String resolveMsg = e.resolveMsg();
        Map header = new HashMap();
        header.put(ServicePacketConstants.STATUS, key);//响应状态码
        header.put(ServicePacketConstants.MSG, resolveMsg); //响应消息
        //报文头
        errorResult.put(ServicePacketConstants.HEADER, header);
        //报文体
        errorResult.put(ServicePacketConstants.BODY, new HashMap());
        return errorResult;
    }
}
