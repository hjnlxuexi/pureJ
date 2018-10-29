package com.lamb.framework.service.flow.helper;

import com.lamb.framework.base.Context;
import com.lamb.framework.base.Framework;
import com.lamb.framework.cache.ConfigCache;
import com.lamb.framework.exception.ServiceRuntimeException;
import com.lamb.framework.service.flow.constant.FlowConfigConstants;
import com.lamb.framework.service.flow.model.Flow;
import com.lamb.framework.service.flow.model.Forward;
import com.lamb.framework.service.flow.model.Step;
import com.lamb.framework.util.BizConfigHotLoading;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Title : 流程配置解析器</p>
 * <p>Description : 解析流程配置</p>
 * <p>Date : 2017/3/2 16:58</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Slf4j
@Component
public class FlowConfigParser {
    /**
     * 解析流程配置
     *
     * @param context 数据总线
     * @return 流程实体对象
     */
    public Flow parseFlowConfig(Context context) {
        long start = System.currentTimeMillis();
        String serviceId = Framework.getProperty("biz.conf.flow") + context.getServiceId();
        //1、缓存中查找对象
        if (ConfigCache.hasConfig(serviceId)){
            log.debug("流程配置文件【"+serviceId+"】，【命中缓存】");
            return (Flow) ConfigCache.getConfig(serviceId);
        }
        if (serviceId.startsWith(BizConfigHotLoading.HTTP_PROTOCOL))//流程配置不存在
            throw new ServiceRuntimeException("2007",this.getClass(),serviceId);

        log.debug("解析流程配置文件【"+serviceId+"】，开始...");
        //2、解析配置文档
        Flow config = this.parseNodes(serviceId + BizConfigHotLoading.LOCAL_CONF_POSTFIX);
        if (config == null || config.getTitle()==null)
            throw new ServiceRuntimeException("2007",this.getClass(),serviceId);
        //3、添加至缓存
        ConfigCache.addConfig(serviceId, config);
        long end = System.currentTimeMillis();
        log.debug("解析流程配置文件【"+serviceId+"】，结束【"+(end-start)+"毫秒】");
        return config;
    }

    /**
     * 解析文档对象为流程对象
     *
     * @param path 文档路径
     * @return 流程对象
     */
    @SuppressWarnings("unchecked")
    public Flow parseNodes(String path) {
        //0、获取配置内容
        Element root = getFlowConfDoc(path);

        if (!root.getName().equals(FlowConfigConstants.FLOW_TAG))//流程文档结构不正确
            throw new ServiceRuntimeException("2004" , this.getClass());
        Flow flow = new Flow();
        //1、获取流程属性
        List<Attribute> attributes = root.attributes();
        for (Attribute attribute : attributes) {
            String attrName = attribute.getName();
            String attrValue = attribute.getValue();
            //流程标题
            if (attrName.equals(FlowConfigConstants.FLOW_TITLE))
                flow.setTitle(attrValue);
        }
        //2、解析步骤实例
        Iterator<Element> iterator = root.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            if (!e.getName().equals(FlowConfigConstants.STEP_TAG))//流程文档结构不正确
                throw new ServiceRuntimeException("2004" , this.getClass());
            Step step = new Step();
            List<Attribute> attrs = e.attributes();
            for (Attribute attribute : attrs) {
                String attrName = attribute.getName();
                String attrValue = attribute.getValue();
                //步骤索引
                if (attrName.equals(FlowConfigConstants.STEP_INDEX)){
                    if (attrValue==null||attrValue.equals("")||flow.getSteps().containsKey(attrValue))//流程文档必须包含步骤索引，且不重复
                        throw new ServiceRuntimeException("2004" , this.getClass());
                    step.setIndex(attrValue);
                }
                //下一步索引
                if (attrName.equals(FlowConfigConstants.STEP_NEXT)){
                    step.setNext(attrValue);
                }
                //步骤映射实体
                if (attrName.equals(FlowConfigConstants.STEP_REF)) {
                    if (attrValue == null || attrValue.equals(""))//流程文档必须包含步骤映射
                        throw new ServiceRuntimeException("2004",this.getClass());
                    step.setRef(attrValue);
                }
                //步骤描述
                if (attrName.equals(FlowConfigConstants.STEP_DESC))
                    step.setDesc(attrValue);
            }
            //3、解析转向实例
            Iterator<Element> iter = e.elementIterator();
            while (iter.hasNext()){
                Element el = iter.next();
                if (!el.getName().equals(FlowConfigConstants.FORWARD_TAG))//流程文档结构不正确
                    throw new ServiceRuntimeException("2004" , this.getClass());
                Forward forward = new Forward();
                List<Attribute> list = el.attributes();
                for (Attribute attribute : list){
                    String attrName = attribute.getName();
                    String attrValue = attribute.getValue();
                    //条件表达式
                    if (attrName.equals(FlowConfigConstants.FORWARD_CONDITION)){
                        if (attrValue == null || attrValue.equals(""))//流程文档必须包含条件表达式
                            throw new ServiceRuntimeException("2004" , this.getClass());
                        forward.setCondition(attrValue);
                    }
                    //转向步骤索引
                    if (attrName.equals(FlowConfigConstants.FORWARD_TO)){
                        if (attrValue == null || attrValue.equals(""))//流程文档必须包含转向索引
                            throw new ServiceRuntimeException("2004" , this.getClass());
                        forward.setTo(attrValue);
                    }
                    //转向描述
                    if (attrName.equals(FlowConfigConstants.FORWARD_DESC))
                        forward.setDesc(attrValue);
                }
                //将转向添加至步骤
                step.getMapping().add(forward);
            }
            //将步骤添加至流程
            flow.getSteps().put(step.getIndex() , step);
        }
        return flow;
    }


    /**
     * 获取配置文档对象
     *
     * @param path 文档路径
     * @return 文档对象
     */
    private Element getFlowConfDoc(String path) {
        SAXReader reader = new SAXReader();
        Document doc;
        try {
            doc = reader.read(new File(path));
        } catch (DocumentException e) {//流程文档解析失败
            throw new ServiceRuntimeException("2003" , this.getClass() , e , path);
        }
        return doc.getRootElement();
    }
}
